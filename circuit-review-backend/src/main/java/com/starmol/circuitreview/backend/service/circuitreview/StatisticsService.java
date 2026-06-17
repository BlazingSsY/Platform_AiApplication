package com.starmol.circuitreview.backend.service.circuitreview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.CircuitReviewStatisticsExportDataDTO;
import com.starmol.circuitreview.backend.bean.dto.DepartmentDTO;
import com.starmol.circuitreview.backend.bean.vo.*;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.common.UserMetaData;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.repository.circuitreview.StatisticsCircuitReviewResultMapper;
import com.starmol.circuitreview.backend.service.circuitreview.feign.UserServiceClient;
import com.starmol.circuitreview.backend.service.common.HttpUtilsService;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import com.starmol.circuitreview.backend.utils.IdWorker;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    @Value("${deployment.type}")
    private Integer deploymentType;

    @Value("${deployment.circuit-service-url}")
    private String circuitServiceUrl;

    private final UserServiceClient userServiceClient;
    private final HttpUtilsService httpUtilsService;
    private final StatisticsCircuitReviewResultMapper circuitReviewResultMapper;
    private final ObjectMapper objectMapper;
    
    // 规则缓存，用于优化规则类型统计
    private static Map<Long, Integer> ruleIdToTypeCache = new ConcurrentHashMap<>();
    private static Map<String, Integer> ruleCodeToTypeCache = new ConcurrentHashMap<>();
    private Map<Long, Map<String, Map<String, Object>>> ruleTypeChartDataMapCache = new ConcurrentHashMap<>();
    private static volatile boolean ruleCacheInitialized = false;
    private static final Object ruleCacheLock = new Object();

    /**
     * 获取统计分析页面数据，基于每个文件的最新版本
     * 调整统计口径：一个文件只考虑最新的一个有审查结果（该版本对应的审查结果的status为2而且isDelete为0）的版本
     * 并增加关闭审查点数这个统计指标
     */
    public CircuitReviewStatisticsDataVO getStatisticsPageDataForLatestVersion(CircuitReviewStatisticsRequestVO requestVO) {
        CircuitReviewStatisticsDataVO result = new CircuitReviewStatisticsDataVO();

        // 检查用户角色
        UserMetaData currentUser = HttpRequestUtil.getUser();
        ResponseWrapper<DepartmentDTO> departmentDTOResponseWrapper = userServiceClient.getDepartmentById(currentUser.getDepartmentId());
        if(!departmentDTOResponseWrapper.isSucc()) {
            log.error("获取部门信息失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentDTOResponseWrapper.getMsg());
            return result;
        }
        DepartmentDTO userDepartment = departmentDTOResponseWrapper.getContent();
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        boolean isNormalUser = SysRoleTypeEnum.NORMAL_USER.equals(userRole) || SysRoleTypeEnum.EXPERT.equals(userRole);
        boolean isDepartmentSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);

        // 获取部门ID列表
        List<Long> departmentIds = requestVO.getDepartmentIds();
        if (CollectionUtils.isEmpty(departmentIds)) {
            return result;
        }

        // 确定查询范围
        Long userId = null;
        if (isNormalUser) {
            userId = currentUser.getId();
        }

        long totalStart = System.currentTimeMillis();
        log.info("开始生成统计分析页面数据（基于最新版本），请求参数: {}", requestVO);

        long deptStart = System.currentTimeMillis();
        ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIds(departmentIds);
        if(!departmentsResponseWrapper.isSucc()) {
            log.error("获取部门列表失败: {} 错误信息: {}", departmentIds, departmentsResponseWrapper.getMsg());
            return result;
        }
        List<DepartmentDTO> departments = departmentsResponseWrapper.getContent();
        // 对部门名称进行中文拼音排序
        Collator collator = Collator.getInstance(Locale.CHINESE);
        departments.sort((d1, d2) -> collator.compare(d1.getName(), d2.getName()));
        long deptEnd = System.currentTimeMillis();
        log.info("departmentService.list耗时: {} ms", (deptEnd - deptStart));

        // 格式化日期
        String startDate = null;
        String endDate = null;
        if (requestVO.getStartDate() != null) {
            startDate = requestVO.getStartDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (requestVO.getEndDate() != null) {
            endDate = requestVO.getEndDate().plusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 处理用户列表
        List<Long> userIds = requestVO.getUserIds();
        if (Objects.nonNull(userId)) {
            if (CollectionUtils.isNotEmpty(userIds)) {
                userIds.add(userId);
            } else {
                userIds = new ArrayList<>();
                userIds.add(userId);
            }
        }

        log.info("查询条件 - 部门IDs: {}, 开始日期: {}, 结束日期: {}", departmentIds, startDate, endDate);

        // 1. 获取文件详情列表（基于最新版本）
        long fileDetailStart = System.currentTimeMillis();
        log.info("开始获取文件详情列表（基于最新版本）");
        List<CircuitFileDetailVO> fileDetails = circuitReviewResultMapper.getStatisticsFileDetailsByLatestVersion(
                departmentIds, userIds, startDate, endDate);

        long fileDetailEnd = System.currentTimeMillis();
        log.info("getStatisticsFileDetailsByLatestVersion耗时: {} ms", (fileDetailEnd - fileDetailStart));
        log.info("获取到文件详情数量: {}", fileDetails.size());
        try {
            log.info("获取到文件详情: {}", objectMapper.writeValueAsString(fileDetails));
        } catch (JsonProcessingException e) {
            log.error("JSON转换失败", e);
        }

        result.setFileDetailVOList(fileDetails);

        // 2. 计算汇总数据
        long summaryStart = System.currentTimeMillis();
        log.info("开始计算汇总数据（基于最新版本）");
        int fileCount = fileDetails.size();
        int totalReviewPointCount = 0;
        int totalPassReviewPointCount = 0;
        int totalFailReviewPointCount = 0;
        int totalClosedFailCheckPoints = 0; // 新增：计算关闭的不通过检查点总数

        for (CircuitFileDetailVO fileDetail : fileDetails) {
            if (fileDetail.getCheckPoints() != null) {
                totalReviewPointCount += fileDetail.getCheckPoints();
            }
            if (fileDetail.getPassCheckPoints() != null) {
                totalPassReviewPointCount += fileDetail.getPassCheckPoints();
            }
            if (fileDetail.getFailCheckPoints() != null) {
                totalFailReviewPointCount += fileDetail.getFailCheckPoints();
            }
            if (fileDetail.getClosedFailCheckPoints() != null) {
                totalClosedFailCheckPoints += fileDetail.getClosedFailCheckPoints();
            }
        }

        BigDecimal totalPassRate = BigDecimal.ZERO;
        if (totalReviewPointCount > 0) {
            totalPassRate = new BigDecimal(totalPassReviewPointCount)
                    .divide(new BigDecimal(totalReviewPointCount), 5, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        result.setFileCount(fileCount);
        result.setTotalReviewPointCount(totalReviewPointCount);
        result.setTotalPassReviewPointCount(totalPassReviewPointCount);
        result.setTotalFailReviewPointCount(totalFailReviewPointCount);
        result.setTotalPassRate(totalPassRate);

        long summaryEnd = System.currentTimeMillis();
        log.info("汇总数据计算耗时: {} ms", (summaryEnd - summaryStart));
        log.info("汇总数据 - 文件数量: {}, 审查点数: {}, 通过审查点数: {}, 通过率: {}%, 关闭审查点数: {}",
                fileCount, totalReviewPointCount, totalPassReviewPointCount, totalPassRate, totalClosedFailCheckPoints);

        // 3. 构建图表数据
        long chartStart = System.currentTimeMillis();
        log.info("开始构建图表数据（基于最新版本）");
        Map<String, Map<String, Map<String, Object>>> chartDataMap = new LinkedHashMap<>();

        // 3.1 按部门统计（保留原有方式，但使用最新版本数据）
        Map<String, Object> deptUserCountMap = new LinkedHashMap<>();
        if (isNormalUser) {
            long deptStatStart = System.currentTimeMillis();
            log.info("开始按普通用户统计（基于最新版本）");
            List<Map<String, Object>> departmentStats = circuitReviewResultMapper.getStatisticsByNormalUserForLatestVersion(
                    userIds, startDate, endDate);
            long deptStatEnd = System.currentTimeMillis();
            log.info("getStatisticsByNormalUserForLatestVersion耗时: {} ms", (deptStatEnd - deptStatStart));
            Map<String, Map<String, Object>> departmentChartData = buildChartData(departmentStats, "user_name");

            // 3.1.1 普通用户按用户统计用户数量，永远为1
            deptUserCountMap.put(currentUser.getName(), 1);
            departmentChartData.put("用户数量", deptUserCountMap);

            for (Map.Entry<String, Map<String, Object>> entry : departmentChartData.entrySet()) {
                if (!entry.getValue().containsKey(currentUser.getName())) {
                    if (entry.getKey().equals("当前文件数量")) {
                        Map<String, Object> innerMap = new HashMap<>();
                        innerMap.put("已审查", 0);
                        innerMap.put("未审查", 0);
                        entry.getValue().put(currentUser.getName(), innerMap);
                    } else {
                        entry.getValue().put(currentUser.getName(), 0);
                    }
                }
            }
            chartDataMap.put("按用户", departmentChartData);

            log.info("按普通用户统计完成，用户数量: {}", departmentStats.size());
        } else {
            long deptStatStart = System.currentTimeMillis();
            log.info("开始按部门统计（基于最新版本）");
            List<Map<String, Object>> departmentStats = circuitReviewResultMapper.getStatisticsByDepartmentForLatestVersion(
                    departmentIds, userIds, startDate, endDate);
            long deptStatEnd = System.currentTimeMillis();
            log.info("getStatisticsByDepartmentForLatestVersion耗时: {} ms", (deptStatEnd - deptStatStart));
            Map<String, Map<String, Object>> departmentChartData = buildChartData(departmentStats, "department_name");

            // 3.1.1 按部门统计用户数量（保留原有方式）
            long userCountStart = System.currentTimeMillis();
            List<Map<String, Object>> userCountByDepartment =
                    circuitReviewResultMapper.getUserCountByDepartment(departmentIds, userIds);
            long userCountEnd = System.currentTimeMillis();
            log.info("getUserCountByDepartment耗时: {} ms", (userCountEnd - userCountStart));

            for (Map<String, Object> map : userCountByDepartment) {
                if (map.containsKey("department_name")) {
                    deptUserCountMap.put(map.get("department_name").toString(), map.get("user_count"));
                }
            }
            departmentChartData.put("用户数量", deptUserCountMap);
            for (DepartmentDTO department : departments) {
                for (Map.Entry<String, Map<String, Object>> entry : departmentChartData.entrySet()) {
                    if (!entry.getValue().containsKey(department.getName())) {
                        if (entry.getKey().equals("当前文件数量")) {
                            Map<String, Object> innerMap = new HashMap<>();
                            innerMap.put("已审查", 0);
                            innerMap.put("未审查", 0);
                            entry.getValue().put(department.getName(), innerMap);
                        } else {
                            entry.getValue().put(department.getName(), 0);
                        }
                    }
                }
            }
            for (Map.Entry<String, Map<String, Object>> entry : departmentChartData.entrySet()) {
                LinkedHashMap<String, Object> sorted = entry.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey(collator)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
                entry.setValue(sorted);
            }
            chartDataMap.put("按单位", departmentChartData);
            log.info("按部门统计完成，部门数量: {}", departmentStats.size());
        }


        // 3.2 631admin用户需要支持按用户统计
        if (StringUtils.isNotEmpty(userDepartment.getName()) && userDepartment.getName().equals("631")) {
            if (isDepartmentSupervisor) {
                long userStatStart = System.currentTimeMillis();
                log.info("开始按631用户统计（基于最新版本）");
                List<Map<String, Object>> userStats = circuitReviewResultMapper.getStatisticsByUserForLatestVersion(
                        departmentIds, userIds, startDate, endDate);
                long userStatEnd = System.currentTimeMillis();
                log.info("getStatisticsByUserForLatestVersion耗时: {} ms", (userStatEnd - userStatStart));
                Map<String, Map<String, Object>> userChartData = buildChartData(userStats, "user_name");

                chartDataMap.put("按用户", userChartData);
                log.info("按用户统计完成，用户数量: {}", userChartData.size());
            }
        }

        // 3.3 一次性获取所有时间维度数据
        long timeStatStart = System.currentTimeMillis();
        log.info("开始一次性获取所有时间维度数据（基于最新版本）");
        List<Map<String, Object>> timeStats = circuitReviewResultMapper.getStatisticsByTimeDimensionsForLatestVersion(
                departmentIds, userIds, startDate, endDate);
        long timeStatEnd = System.currentTimeMillis();
        log.info("getStatisticsByTimeDimensionsForLatestVersion耗时: {} ms", (timeStatEnd - timeStatStart));

        // 按不同维度分组
        Map<String, Map<String, Object>> dayChartData = buildChartData(timeStats, "review_day");
        chartDataMap.put("按时间-日", dayChartData);
        log.info("按日统计完成，天数: {}", dayChartData.values().stream().findFirst().map(Map::size).orElse(0));

        Map<String, Map<String, Object>> weekChartData = buildChartData(timeStats, "review_week");
        chartDataMap.put("按时间-周", weekChartData);
        log.info("按周统计完成，周数: {}", weekChartData.values().stream().findFirst().map(Map::size).orElse(0));

        Map<String, Map<String, Object>> monthChartData = buildChartData(timeStats, "review_month");
        chartDataMap.put("按时间-月", monthChartData);
        log.info("按月统计完成，月数: {}", monthChartData.values().stream().findFirst().map(Map::size).orElse(0));

        Map<String, Map<String, Object>> quarterChartData = buildChartData(timeStats, "review_quarter");
        chartDataMap.put("按时间-季", quarterChartData);
        log.info("按季统计完成，季数: {}", quarterChartData.values().stream().findFirst().map(Map::size).orElse(0));

        Map<String, Map<String, Object>> yearChartData = buildChartData(timeStats, "review_year");
        chartDataMap.put("按时间-年", yearChartData);
        log.info("按年统计完成，年数: {}", yearChartData.values().stream().findFirst().map(Map::size).orElse(0));

        Map<String, Map<String, Object>> ruleTypeChartData = new LinkedHashMap<>();
        chartDataMap.put("按规则类型", ruleTypeChartData);
        result.setChartDataMap(chartDataMap);

        long chartEnd = System.currentTimeMillis();
        log.info("图表数据构建耗时: {} ms", (chartEnd - chartStart));
        log.info("统计分析页面数据（基于最新版本）生成完成");
        long totalEnd = System.currentTimeMillis();
        log.info("getStatisticsPageDataForLatestVersion总耗时: {} ms", (totalEnd - totalStart));
        result.setResultId(IdWorker.getId());
        StatisticsService statisticsService = SpringContextUtils.getInstanceByType(StatisticsService.class);
        statisticsService.asyncGetRuleTypeChartData(result.getResultId(), fileDetails, departmentIds, userIds, startDate, endDate);
        return result;
    }

    @Async
    public void asyncGetRuleTypeChartData(Long resultId, List<CircuitFileDetailVO> fileDetails, List<Long> departmentIds, List<Long> userIds, String startDate, String endDate) {
        // 3.4 按规则类型统计（基于最新版本）- 优化版本
        long ruleTypeStatStart = System.currentTimeMillis();
        log.info("开始按规则类型统计（基于最新版本）");
        List<Map<String, Object>> ruleTypeStats = circuitReviewResultMapper.getStatisticsByRuleTypeForLatestVersion(
                departmentIds, userIds, startDate, endDate);;
        long ruleTypeStatEnd = System.currentTimeMillis();
        log.info("getStatisticsByRuleTypeForLatestVersion耗时: {} ms", (ruleTypeStatEnd - ruleTypeStatStart));
        Map<String, Map<String, Object>> ruleTypeChartData = buildChartData(ruleTypeStats, "rule_type");
        ruleTypeChartDataMapCache.put(resultId, ruleTypeChartData);
        log.info("按规则类型统计完成，规则类型数: {}", ruleTypeStats.size());
    }

    public Map<String, Map<String, Object>> getRuleTypeChartDataByResultId(Long resultId) {
        if (ruleTypeChartDataMapCache.containsKey(resultId)) {
            Map<String, Map<String, Object>> map = ruleTypeChartDataMapCache.get(resultId);
            ruleTypeChartDataMapCache.remove(resultId);
            return map;
        }
        else {
            return new LinkedHashMap<>();
        }
    }

    /**
     * 预加载规则数据到缓存
     */
    private void loadRuleCacheIfNotLoaded() {
        if (!ruleCacheInitialized) {
            synchronized (ruleCacheLock) {
                if (!ruleCacheInitialized) {
                    log.info("开始加载规则缓存");
                    long cacheStart = System.currentTimeMillis();
                    
                    try {
                        // 从数据库加载规则数据
                        List<Map<String, Object>> allRules = circuitReviewResultMapper.getAllRules();
                        
                        for (Map<String, Object> rule : allRules) {
                            Long ruleId = (Long) rule.get("id");
                            String ruleCode = (String) rule.get("code");
                            Integer ruleType = (Integer) rule.get("type");
                            
                            if (ruleId != null && ruleType != null) {
                                ruleIdToTypeCache.put(ruleId, ruleType);
                            }
                            if (ruleCode != null && ruleType != null) {
                                ruleCodeToTypeCache.put(ruleCode, ruleType);
                            }
                        }
                        
                        long cacheEnd = System.currentTimeMillis();
                        log.info("规则缓存加载完成，耗时: {} ms，规则数量: {}", 
                                (cacheEnd - cacheStart), allRules.size());
                    } catch (Exception e) {
                        log.error("加载规则缓存失败", e);
                    }
                    
                    ruleCacheInitialized = true;
                }
            }
        }
    }

    /**
     * 构建图表数据
     *
     * @param stats        统计数据列表
     * @param dimensionKey 维度键名
     * @return 图表数据
     */
    private Map<String, Map<String, Object>> buildChartData(List<Map<String, Object>> stats, String dimensionKey) {
        Map<String, Map<String, Object>> chartData = new LinkedHashMap<>();

        // 定义指标键名
        String[] metricKeys = {"file_count", "current_file_count", "total_rule_count", "pass_rule_count", "fail_rule_count", "closed_loop_count", "non_closed_loop_count", "closed_fail_check_points"};
        String[] currentFileCountMetricKeys = {"current_file_count_reviewed", "current_file_count_not_reviewed"};
        String avgKey = "average_pass_rate";

        // 初始化指标Map
        for (String metricKey : metricKeys) {
            chartData.put(getMetricDisplayName(metricKey), new LinkedHashMap<>());
        }
        chartData.put(getMetricDisplayName(avgKey), new LinkedHashMap<>());

        // 临时存储用于加权平均的分子分母
        Map<String, Long> totalRuleCountMap = new HashMap<>();
        Map<String, Long> passRuleCountMap = new HashMap<>();

        // 累加各项数值
        for (Map<String, Object> stat : stats) {
            String dimensionValue = String.valueOf(stat.get(dimensionKey));
            if (dimensionValue != null && !"null".equals(dimensionValue)) {
                if (dimensionKey.equals("rule_type")) {
                    RuleTypeEnum ruleTypeEnum = RuleTypeEnum.getByValue(Integer.valueOf(dimensionValue));
                    if (ruleTypeEnum != null) {
                        dimensionValue = ruleTypeEnum.getName();
                    }
                }
                for (String metricKey : metricKeys) {
                    if (metricKey.equals("current_file_count")) {
                        // 当前文件数量的map
                        Map<String, Object> metricMap = chartData.get(getMetricDisplayName(metricKey));
                        Map<String, Object> innerMetricMap = (Map<String, Object>) metricMap.getOrDefault(dimensionValue, new HashMap<>());
                        for (String currentFileCountMetricKey : currentFileCountMetricKeys) {
                            Object value = stat.get(currentFileCountMetricKey);
                            if (value != null) {
                                String metricDisplayName = getMetricDisplayName(currentFileCountMetricKey);
                                long oldValue = innerMetricMap.getOrDefault(metricDisplayName, 0L) instanceof Number ? ((Number) innerMetricMap.getOrDefault(metricDisplayName, 0L)).longValue() : 0L;
                                innerMetricMap.put(metricDisplayName, oldValue + ((Number) value).longValue());
                            } else {
                                String metricDisplayName = getMetricDisplayName(currentFileCountMetricKey);
                                if (!innerMetricMap.containsKey(metricDisplayName)) {
                                    innerMetricMap.put(getMetricDisplayName(currentFileCountMetricKey), 0L);
                                }
                            }
                        }
                        metricMap.put(dimensionValue, innerMetricMap);
                    } else {
                        Object value = stat.get(metricKey);
                        if (value != null) {
                            Map<String, Object> metricMap = chartData.get(getMetricDisplayName(metricKey));
                            long oldValue = metricMap.getOrDefault(dimensionValue, 0L) instanceof Number ? ((Number) metricMap.getOrDefault(dimensionValue, 0L)).longValue() : 0L;
                            metricMap.put(dimensionValue, oldValue + ((Number) value).longValue());
                            // 记录用于平均通过率的分子分母
                            if ("total_rule_count".equals(metricKey)) {
                                totalRuleCountMap.put(dimensionValue, totalRuleCountMap.getOrDefault(dimensionValue, 0L) + ((Number) value).longValue());
                            }
                            if ("pass_rule_count".equals(metricKey)) {
                                passRuleCountMap.put(dimensionValue, passRuleCountMap.getOrDefault(dimensionValue, 0L) + ((Number) value).longValue());
                            }
                        } else {
                            String metricDisplayName = getMetricDisplayName(metricKey);
                            if (!chartData.containsKey(metricDisplayName)) {
                                chartData.get(metricDisplayName).put(dimensionValue, 0L);
                            }
                        }
                    }
                }
            }
        }
        // 计算加权平均通过率
        Map<String, Object> avgMap = chartData.get(getMetricDisplayName(avgKey));
        for (String dimensionValue : totalRuleCountMap.keySet()) {
            long total = totalRuleCountMap.getOrDefault(dimensionValue, 0L);
            long pass = passRuleCountMap.getOrDefault(dimensionValue, 0L);
            BigDecimal avgPassRate = BigDecimal.ZERO;
            if (total > 0) {
                avgPassRate = new BigDecimal(pass)
                        .divide(new BigDecimal(total), 5, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            avgMap.put(dimensionValue, avgPassRate);
        }
        // 对按日统计的第二层Map按键进行字典序正序排序
        if (dimensionKey != null && dimensionKey.startsWith("review_")) {
            for (Map.Entry<String, Map<String, Object>> entry : chartData.entrySet()) {
                entry.setValue(new TreeMap<>(entry.getValue()));
            }
        }
        return chartData;
    }

    /**
     * 获取指标显示名称
     *
     * @param metricKey 指标键名
     * @return 显示名称
     */
    private String getMetricDisplayName(String metricKey) {
        return switch (metricKey) {
            case "file_count" -> "已审文件数量";
            case "current_file_count" -> "当前文件数量";
            case "current_file_count_reviewed" -> "已审查";
            case "current_file_count_not_reviewed" -> "未审查";
            case "total_rule_count" -> "审查点数";
            case "pass_rule_count" -> "通过审查点数";
            case "fail_rule_count" -> "问题点数";
            case "closed_loop_count" -> "已关闭数量";
            case "non_closed_loop_count" -> "未关闭数量";
            case "closed_fail_check_points" -> "关闭审查点数";
            case "average_pass_rate" -> "平均通过率(%)";
            default -> metricKey;
        };
    }

    public List<CircuitReviewStatisticsExportDataDTO> getStatisticsForExport(StatisticsExportRequestVO requestVO) {
        log.info("开始执行统计导出，请求参数: {}", requestVO);
        long startTime = System.currentTimeMillis();

        // 1. 获取当前用户信息并检查权限
        UserMetaData currentUser = HttpRequestUtil.getUser();
        if (currentUser == null) {
            log.error("获取当前用户信息失败");
            throw new RuntimeException("获取当前用户信息失败");
        }
        
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        log.info("当前用户角色: {}, 用户ID: {}, 部门ID: {}", userRole, currentUser.getId(), currentUser.getDepartmentId());
        
        // 2. 权限检查：只允许管理员、机载领导和领导调用
        boolean isAdmin = SysRoleTypeEnum.ADMIN.equals(userRole);
        boolean isSuperSupervisor = SysRoleTypeEnum.SUPER_SUPERVISOR.equals(userRole);
        boolean isOrgSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);
        
        if (!isAdmin && !isSuperSupervisor && !isOrgSupervisor) {
            log.error("当前用户无权限导出统计数据，用户角色: {}", userRole);
            throw new RuntimeException("当前用户无权限导出统计数据");
        }
        
        // 3. 确定可访问的部门 ID 列表
        List<Long> accessibleDepartmentIds;
        if (isAdmin || isSuperSupervisor) {
            // 管理员和机载领导可以访问所有部门，传 null 表示不限制
            accessibleDepartmentIds = null;
            log.info("用户为管理员或机载领导，可访问所有部门");
        } else {
            // 领导只能访问本部门及所有下级部门
            log.info("用户为领导，获取本部门及子部门列表，部门ID: {}", currentUser.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(currentUser.getDepartmentId());
            if (!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentsResponseWrapper.getMsg());
                throw new RuntimeException("获取部门信息失败: " + departmentsResponseWrapper.getMsg());
            }
            List<DepartmentDTO> departments = departmentsResponseWrapper.getContent();
            accessibleDepartmentIds = departments.stream()
                    .map(DepartmentDTO::getId)
                    .collect(Collectors.toList());
            log.info("领导可访问的部门ID列表: {}", accessibleDepartmentIds);
        }

        // 格式化日期
        String startDate = null;
        String endDate = null;
        if (requestVO.getStartDate() != null) {
            startDate = requestVO.getStartDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (requestVO.getEndDate() != null) {
            endDate = requestVO.getEndDate().plusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        log.info("格式化后的日期范围: {} - {}", startDate, endDate);

        // 4. 调用Mapper方法获取统计数据（传入部门 ID 列表进行过滤）
        long dbStartTime = System.currentTimeMillis();
        List<Map<String, Object>> exportDataList = circuitReviewResultMapper.getStatisticsForExport(accessibleDepartmentIds, startDate, endDate);
        long dbEndTime = System.currentTimeMillis();
        log.info("数据库查询完成，耗时: {}ms，查询结果数量: {}", (dbEndTime - dbStartTime), exportDataList.size());

        // 调用新的函数获取超半月未关闭原理图数据（传入部门 ID 列表进行过滤）
        List<Map<String, Object>> exceedHalfMonthNotClosedData = circuitReviewResultMapper.getExceedHalfMonthNotClosedCount(accessibleDepartmentIds);
        
        // 创建一个Map用于快速查找超半月未关闭数据
        Map<Long, Integer> exceedHalfMonthNotClosedMap = new HashMap<>();
        Set<Long> allDepartmentsWithExceedData = new HashSet<>(); // 记录所有有超半月未关闭数据的部门ID
        for (Map<String, Object> data : exceedHalfMonthNotClosedData) {
            Long departmentId = ((Number) data.get("department_id")).longValue();
            Integer exceedCount = ((Number) data.get("exceed_count")).intValue();
            exceedHalfMonthNotClosedMap.put(departmentId, exceedCount);
            allDepartmentsWithExceedData.add(departmentId);
        }
        
        // 创建一个Map用于快速查找原有统计数据
        Map<Long, Map<String, Object>> exportDataMap = new HashMap<>();
        for (Map<String, Object> exportData : exportDataList) {
            if (exportData.get("department_id") != null) {
                Long departmentId = ((Number) exportData.get("department_id")).longValue();
                exportDataMap.put(departmentId, exportData);
            }
        }
        
        // 获取所有涉及的部门ID（包括有统计数据的部门和有超半月未关闭数据的部门）
        Set<Long> allDepartmentIds = new HashSet<>();
        allDepartmentIds.addAll(exportDataMap.keySet());
        allDepartmentIds.addAll(allDepartmentsWithExceedData);
        
        // 转换为DTO对象
        List<CircuitReviewStatisticsExportDataDTO> result = new ArrayList<>();
        for (Long departmentId : allDepartmentIds) {
            CircuitReviewStatisticsExportDataDTO dto = new CircuitReviewStatisticsExportDataDTO();
            
            // 检查是否有该部门的统计数据
            Map<String, Object> departmentExportData = exportDataMap.get(departmentId);
            
            if (departmentExportData != null) {
                // 如果存在统计数据，则使用统计数据中的值
                // 设置部门ID
                if (departmentExportData.get("department_id") != null) {
                    dto.setDepartmentId(((Number) departmentExportData.get("department_id")).longValue());
                }
                
                // 设置部门名称
                if (departmentExportData.get("department_name") != null) {
                    dto.setDepartmentName((String) departmentExportData.get("department_name"));
                }
                
                // 设置文件数量
                if (departmentExportData.get("file_counts") != null) {
                    dto.setFileCounts(((Number) departmentExportData.get("file_counts")).intValue());
                }
                
                // 设置总审查点数量
                if (departmentExportData.get("total_check_points") != null) {
                    dto.setTotalCheckPoints(((Number) departmentExportData.get("total_check_points")).intValue());
                }
                
                // 设置总通过的审查点数量
                if (departmentExportData.get("total_pass_check_points") != null) {
                    dto.setTotalPassCheckPoints(((Number) departmentExportData.get("total_pass_check_points")).intValue());
                }
                
                // 设置总不通过的审查点数量
                if (departmentExportData.get("total_fail_check_points") != null) {
                    dto.setTotalFailCheckPoints(((Number) departmentExportData.get("total_fail_check_points")).intValue());
                }
                
                // 设置总关闭审查点数量
                if (departmentExportData.get("total_closed_fail_check_points") != null) {
                    // 确保关闭的审查点数不为负数
                    int closedPoints = ((Number) departmentExportData.get("total_closed_fail_check_points")).intValue();
                    dto.setTotalClosedFailCheckPoints(Math.max(0, closedPoints));
                }
                
                // 设置问题已关闭文件数量
                if (departmentExportData.get("closed_loop_file_counts") != null) {
                    dto.setClosedLoopFileCounts(((Number) departmentExportData.get("closed_loop_file_counts")).intValue());
                }
            } else {
                // 如果不存在统计数据，则其他字段设置为null（显示为"-"）
                dto.setDepartmentId(departmentId);
                
                // 为了获取部门名称，需要额外查询或从超半月未关闭数据中获取
                for (Map<String, Object> data : exceedHalfMonthNotClosedData) {
                    if (((Number) data.get("department_id")).longValue() == departmentId) {
                        dto.setDepartmentName((String) data.get("department_name"));
                        break;
                    }
                }
                
                dto.setFileCounts(null);
                dto.setTotalCheckPoints(null);
                dto.setTotalPassCheckPoints(null);
                dto.setTotalFailCheckPoints(null);
                dto.setTotalClosedFailCheckPoints(null);
                dto.setClosedLoopFileCounts(null);
            }
            
            // 设置超半月未关闭原理图数据（从新查询结果中获取）
            if (exceedHalfMonthNotClosedMap.containsKey(departmentId)) {
                dto.setExceedHalfMonthNotClosedFiles(exceedHalfMonthNotClosedMap.get(departmentId));
            } else {
                dto.setExceedHalfMonthNotClosedFiles(0); // 如果没有超半月未关闭数据，则设置为0
            }
            
            result.add(dto);
        }
        log.info("本地数据转换完成，转换数据数量: {}", result.size());

        if (deploymentType == 0) {
            try {
                log.info("当前为机载部署，需要远程获取631的统计数据");
                // 机载部署需要远程获取631的统计数据
                String targetURI = String.format("%s/circuitreview/v1/circuit-statistics/export-data", circuitServiceUrl);
                log.info("准备调用外部接口，URI: {}", targetURI);
                long externalStartTime = System.currentTimeMillis();
                List<CircuitReviewStatisticsExportDataDTO> statisticsForExportFromExternal = getStatisticsForExportFromExternal(requestVO, targetURI);
                statisticsForExportFromExternal.forEach(circuitReviewStatisticsExportDataDTO -> circuitReviewStatisticsExportDataDTO.setDepartmentName(circuitReviewStatisticsExportDataDTO.getDepartmentName() + "_远程"));
                long externalEndTime = System.currentTimeMillis();
                log.info("外部接口调用完成，耗时: {}ms，获取数据数量: {}", (externalEndTime - externalStartTime), statisticsForExportFromExternal.size());
                result.addAll(statisticsForExportFromExternal);
            } catch (Exception e) {
                log.error("远程获取631的统计数据失败: {}", e.getMessage(), e);
            }
        } else {
            log.info("当前为非机载部署，跳过外部接口调用，部署类型: {}", deploymentType);
        }

        long endTime = System.currentTimeMillis();
        log.info("统计导出完成，总耗时: {}ms，最终数据数量: {}", (endTime - startTime), result.size());
        return result;
    }

    @SneakyThrows
    public List<CircuitReviewStatisticsExportDataDTO> getStatisticsForExportFromExternal(StatisticsExportRequestVO requestVO, String targetURI) {
        log.info("开始调用外部统计导出接口，请求参数: {}，目标URI: {}", requestVO, targetURI);
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = HttpRequestUtil.getRequest();
        String authorization = request.getHeader("Authorization");
        log.info("获取到请求授权信息，长度: {}", authorization != null ? authorization.length() : 0);

        long httpStartTime = System.currentTimeMillis();
        String responseStr = httpUtilsService.postJsonString(objectMapper.writeValueAsString(requestVO), targetURI, authorization);
        long httpEndTime = System.currentTimeMillis();
        log.info("HTTP请求完成，耗时: {}ms，响应字符串长度: {}", (httpEndTime - httpStartTime), responseStr != null ? responseStr.length() : 0);

        log.info("开始解析响应数据");
        // 解析响应字符串为ResponseWrapper<List<CircuitReviewStatisticsExportDataDTO>>
        ResponseWrapper<List<CircuitReviewStatisticsExportDataDTO>> responseWrapper = 
            objectMapper.readValue(responseStr, 
                objectMapper.getTypeFactory()
                    .constructType(new TypeReference<ResponseWrapper<List<CircuitReviewStatisticsExportDataDTO>>>() {}.getType()));
        log.info("响应数据解析完成");

        // 判断接口调用是否成功
        if (responseWrapper.isSucc()) {
            log.info("外部接口调用成功，数据数量: {}", responseWrapper.getContent() != null ? responseWrapper.getContent().size() : 0);
            long endTime = System.currentTimeMillis();
            log.info("外部统计导出接口调用完成，总耗时: {}ms", (endTime - startTime));
            // 接口调用成功，返回content
            return responseWrapper.getContent();
        } else {
            // 接口调用失败，记录错误日志，返回空list
            log.error("请求631部署数据失败，错误信息: {}", responseWrapper.getMsg());
            long endTime = System.currentTimeMillis();
            log.info("外部统计导出接口调用完成（失败），总耗时: {}ms", (endTime - startTime));
            return Collections.emptyList();
        }
    }

    public List<CircuitFileDetailVO> getStatisticsFileDetailsByLatestVersion(CircuitReviewStatisticsRequestVO requestVO) {
        // 检查用户角色
        UserMetaData currentUser = HttpRequestUtil.getUser();
        ResponseWrapper<DepartmentDTO> departmentDTOResponseWrapper = userServiceClient.getDepartmentById(currentUser.getDepartmentId());
        if(!departmentDTOResponseWrapper.isSucc()) {
            log.error("获取部门信息失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentDTOResponseWrapper.getMsg());
            return Collections.emptyList();
        }
        DepartmentDTO userDepartment = departmentDTOResponseWrapper.getContent();
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        boolean isNormalUser = SysRoleTypeEnum.NORMAL_USER.equals(userRole);
        boolean isDepartmentSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);

        // 获取部门ID列表
        List<Long> departmentIds = requestVO.getDepartmentIds();
        if (CollectionUtils.isEmpty(departmentIds)) {
            return Collections.emptyList();
        }

        // 确定查询范围
        Long userId = null;
        if (isNormalUser) {
            userId = currentUser.getId();
        }

        long totalStart = System.currentTimeMillis();
        log.info("开始生成统计分析页面数据（基于最新版本），请求参数: {}", requestVO);

        long deptStart = System.currentTimeMillis();
        ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIds(departmentIds);
        if(!departmentsResponseWrapper.isSucc()) {
            log.error("获取部门列表失败: {} 错误信息: {}", departmentIds, departmentsResponseWrapper.getMsg());
            return Collections.emptyList();
        }
        List<DepartmentDTO> departments = departmentsResponseWrapper.getContent();
        // 对部门名称进行中文拼音排序
        Collator collator = Collator.getInstance(Locale.CHINESE);
        departments.sort((d1, d2) -> collator.compare(d1.getName(), d2.getName()));
        long deptEnd = System.currentTimeMillis();
        log.info("departmentService.list耗时: {} ms", (deptEnd - deptStart));

        // 格式化日期
        String startDate = null;
        String endDate = null;
        if (requestVO.getStartDate() != null) {
            startDate = requestVO.getStartDate().atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (requestVO.getEndDate() != null) {
            endDate = requestVO.getEndDate().plusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 处理用户列表
        List<Long> userIds = requestVO.getUserIds();
        if (Objects.nonNull(userId)) {
            if (CollectionUtils.isNotEmpty(userIds)) {
                userIds.add(userId);
            } else {
                userIds = new ArrayList<>();
                userIds.add(userId);
            }
        }

        log.info("查询条件 - 部门IDs: {}, 开始日期: {}, 结束日期: {}", departmentIds, startDate, endDate);

        // 1. 获取文件详情列表（基于最新版本）
        long fileDetailStart = System.currentTimeMillis();
        log.info("开始获取文件详情列表（基于最新版本）");
        return circuitReviewResultMapper.getStatisticsFileDetailsByLatestVersion(
                departmentIds, userIds, startDate, endDate);
    }
}
