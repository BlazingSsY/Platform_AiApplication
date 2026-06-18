package com.starmol.sourcecodereview.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sourcecodereview.bean.dto.DepartmentDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeReviewStatisticsExportDataDTO;
import com.starmol.sourcecodereview.bean.dto.UserDTO;
import com.starmol.sourcecodereview.bean.vo.*;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.common.UserMetaData;
import com.starmol.sourcecodereview.constant.SysRoleTypeEnum;
import com.starmol.sourcecodereview.repository.codereview.SourceCodeReviewResultMapper;
import com.starmol.sourcecodereview.service.StatisticsService;
import com.starmol.sourcecodereview.service.common.HttpUtilsService;
import com.starmol.sourcecodereview.service.feign.UserServiceClient;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/**
 * 代码审查统计服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    @Value("${deployment.type}")
    private Integer deploymentType;

    @Value("${deployment.source-code-service-url}")
    private String circuitServiceUrl;


    private final SourceCodeReviewResultMapper sourceCodeReviewResultMapper;
    private final UserServiceClient  userServiceClient;
    private final HttpUtilsService httpUtilsService;
    private final ObjectMapper objectMapper;

    @Override
    public SourceCodeReviewHomeStatisticsDataVO getHomeStatistics() {
        long totalStart = System.currentTimeMillis();
        SourceCodeReviewHomeStatisticsDataVO result = new SourceCodeReviewHomeStatisticsDataVO();

        // 获取当前用户信息
        long userStart = System.currentTimeMillis();
        UserMetaData currentUser = HttpRequestUtil.getUser();
        long userEnd = System.currentTimeMillis();
        log.info("获取当前用户信息耗时: {} ms", (userEnd - userStart));
        if (currentUser == null) {
            log.warn("获取当前用户信息失败");
            return result;
        }

        long userByIdStart = System.currentTimeMillis();
        ResponseWrapper<UserDTO> userDTOResponseWrapper = userServiceClient.getUserById(currentUser.getId());
        if(!userDTOResponseWrapper.isSucc()) {
            log.error("获取当前用户信息失败: {} 错误信息: {}", currentUser.getId(),  userDTOResponseWrapper.getMsg());
            return result;
        }
        UserDTO user = userDTOResponseWrapper.getContent();

        long userByIdEnd = System.currentTimeMillis();
        log.info("userService.getById耗时: {} ms", (userByIdEnd - userByIdStart));
        if (user == null) {
            log.warn("用户不存在: {}", currentUser.getId());
            return result;
        }

        long roleStart = System.currentTimeMillis();
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        long roleEnd = System.currentTimeMillis();
        log.info("roleService.getSysRoleTypeByUserId耗时: {} ms", (roleEnd - roleStart));
        if (userRole == null) {
            log.warn("用户没有角色: {}", user.getId());
            return result;
        }

        // 确定查询范围
        List<Long> departmentIds = new ArrayList<>();
        Long userId = null;

        // 检查用户角色
        boolean isNormalUser = SysRoleTypeEnum.NORMAL_USER.equals(userRole);
        boolean isDepartmentSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);
        List<DepartmentDTO> departments;
        long deptStart = System.currentTimeMillis();
        if (isNormalUser) {
            // 普通用户：只查看自己上传和审查的文件
            userId = user.getId();
            log.info("获取用户当前部门信息，部门ID: {}", user.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsById(user.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门列表失败: {} 错误信息: {}", user.getDepartmentId(), departmentsResponseWrapper.getMsg());
                return result;
            }
            departments = departmentsResponseWrapper.getContent();
        } else if (isDepartmentSupervisor) {
            // 部门负责人：查看全部门（包括子部门）的文件
            log.info("获取部门及其子部门ID列表，部门ID: {}", user.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(user.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", user.getDepartmentId(), departmentsResponseWrapper.getMsg());
                return result;
            }
            departments = departmentsResponseWrapper.getContent();
            departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
        } else {
            // 部门管理员或其他角色：查看全部门（包括子部门）的文件
            log.info("获取部门及其子部门ID列表，部门ID: {}", user.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(user.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", user.getDepartmentId(), departmentsResponseWrapper.getMsg());
                return result;
            }
            departments = departmentsResponseWrapper.getContent();
            departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
        }
        long deptEnd = System.currentTimeMillis();
        log.info("departmentService.list耗时: {} ms", (deptEnd - deptStart));

        // 获取最近审查的5个文件
        long recentStart = System.currentTimeMillis();
        log.info("开始获取最近审查的5个文件");
        List<SourceCodeReviewHomeStatisticsDataItemVO> recentlyReviewedFiles =
                sourceCodeReviewResultMapper.getRecentlyReviewedFiles(departmentIds, userId, 5);
        long recentEnd = System.currentTimeMillis();
        log.info("getRecentlyReviewedFiles耗时: {} ms", (recentEnd - recentStart));

        // 获取通过率最高的5个文件
        long passStart = System.currentTimeMillis();
        log.info("开始获取通过率最高的5个文件");
        List<SourceCodeReviewHomeStatisticsDataItemVO> highestPassRateFiles =
                sourceCodeReviewResultMapper.getHighestPassRateFiles(departmentIds, userId, 5);
        long passEnd = System.currentTimeMillis();
        log.info("getHighestPassRateFiles耗时: {} ms", (passEnd - passStart));

        // 获取按部门统计的全部文件数量
        long totalFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的全部文件数量");
        List<Map<String, Object>> totalFilesCountByDepartment =
                sourceCodeReviewResultMapper.getTotalFilesCountByDepartment(departmentIds, userId);
        long totalFilesEnd = System.currentTimeMillis();
        log.info("getTotalFilesCountByDepartment耗时: {} ms", (totalFilesEnd - totalFilesStart));

        // 获取按部门统计的审查文件数量
        long reviewedFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的审查文件数量");
        List<Map<String, Object>> reviewedFilesCountByDepartment =
                sourceCodeReviewResultMapper.getReviewedFilesCountByDepartment(departmentIds, userId);
        long reviewedFilesEnd = System.currentTimeMillis();
        log.info("getReviewedFilesCountByDepartment耗时: {} ms", (reviewedFilesEnd - reviewedFilesStart));

        // 获取按部门统计的用户数量
        long userCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的用户数量");
        List<Map<String, Object>> userCountByDepartment =
                sourceCodeReviewResultMapper.getUserCountByDepartment(departmentIds, userId);
        long userCountEnd = System.currentTimeMillis();
        log.info("getUserCountByDepartment耗时: {} ms", (userCountEnd - userCountStart));

        // 获取按部门统计的审核不通过规则数量
        long issueCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的审核不通过规则数量");
        List<Map<String, Object>> reviewIssueCountByDepartment =
                sourceCodeReviewResultMapper.getReviewIssueCountByDepartment(departmentIds, userId);
        long issueCountEnd = System.currentTimeMillis();
        log.info("getReviewIssueCountByDepartment耗时: {} ms", (issueCountEnd - issueCountStart));

        // 转换统计结果为Map格式
        long mapStart = System.currentTimeMillis();
        Map<String, Integer> totalFilesCountMap = new HashMap<>();
        Map<String, Integer> reviewedFilesCountMap = new HashMap<>();
        Map<String, Integer> userCountMap = new HashMap<>();
        Map<String, Integer> reviewIssueCountMap = new HashMap<>();


        if(CollectionUtils.isNotEmpty(departments)) {
            // 处理全部文件数量统计
            for (Map<String, Object> item : totalFilesCountByDepartment) {
                String departmentName = (String) item.get("department_name");
                Integer fileCount = ((Number) item.get("file_count")).intValue();
                if (departmentName != null) {
                    totalFilesCountMap.put(departmentName, fileCount);
                }
            }

            // 处理审查文件数量统计
            for (Map<String, Object> item : reviewedFilesCountByDepartment) {
                String departmentName = (String) item.get("department_name");
                Integer fileCount = ((Number) item.get("file_count")).intValue();
                if (departmentName != null) {
                    reviewedFilesCountMap.put(departmentName, fileCount);
                }
            }

            // 处理审核不通过规则数量统计
            for (Map<String, Object> item : reviewIssueCountByDepartment) {
                String departmentName = (String) item.get("department_name");
                Integer issueCount = ((Number) item.get("issue_count")).intValue();
                if (departmentName != null) {
                    reviewIssueCountMap.put(departmentName, issueCount);
                }
            }

            for (DepartmentDTO department : departments) {
                if(!totalFilesCountMap.containsKey(department.getName())) {
                    totalFilesCountMap.put(department.getName(), 0);
                }
                if(!reviewedFilesCountMap.containsKey(department.getName())) {
                    reviewedFilesCountMap.put(department.getName(), 0);
                }
                if(!reviewIssueCountMap.containsKey(department.getName())) {
                    reviewIssueCountMap.put(department.getName(), 0);
                }
            }
        }
        long mapEnd = System.currentTimeMillis();
        log.info("Map数据处理耗时: {} ms", (mapEnd - mapStart));

        // 处理用户数量统计
        long userMapStart = System.currentTimeMillis();
        if (isNormalUser) {
            // 普通用户：当前部门用户数量显示为1
            ResponseWrapper<DepartmentDTO> departmentDTOResponseWrapper = userServiceClient.getDepartmentById(user.getDepartmentId());
            if(!departmentDTOResponseWrapper.isSucc()) {
                log.error("获取部门信息失败: {} 错误信息: {}", user.getDepartmentId(), departmentDTOResponseWrapper.getMsg());
                return result;
            }
            DepartmentDTO userDepartment = departmentDTOResponseWrapper.getContent();
            if (userDepartment != null) {
                userCountMap.put(userDepartment.getName(), 1);
            } else {
                // 如果部门信息不存在，使用默认名称
                userCountMap.put("当前部门", 1);
            }
        } else {
            // 部门负责人或其他角色：显示实际用户数量
            for (Map<String, Object> item : userCountByDepartment) {
                String departmentName = (String) item.get("department_name");
                Integer userCount = ((Number) item.get("user_count")).intValue();
                if (departmentName != null) {
                    userCountMap.put(departmentName, userCount);
                }
            }
        }
        long userMapEnd = System.currentTimeMillis();
        log.info("用户数量Map处理耗时: {} ms", (userMapEnd - userMapStart));

        result.setMostRecentlyReviewedFiles(recentlyReviewedFiles);
        result.setHighestPassRateFiles(highestPassRateFiles);
        result.setTotalFilesCountByDepartment(totalFilesCountMap);
        result.setReviewedFilesCountByDepartment(reviewedFilesCountMap);
        result.setUserCountByDepartment(userCountMap);
        result.setReviewIssueCountByDepartment(reviewIssueCountMap);

        long totalEnd = System.currentTimeMillis();
        log.info("getHomeStatistics总耗时: {} ms", (totalEnd - totalStart));
        return result;
    }

    @Override
    public SourceCodeReviewStatisticsDataVO getSourceCodeReviewStatistics(SourceCodeReviewStatisticsRequestVO requestVO) {
        long totalStart = System.currentTimeMillis();
        log.info("开始生成统计分析页面数据，请求参数: {}", requestVO);

        SourceCodeReviewStatisticsDataVO result = new SourceCodeReviewStatisticsDataVO();

        // 获取部门ID列表
        List<Long> departmentIds = requestVO.getDepartmentIds();
        if(CollectionUtils.isEmpty(departmentIds)) {
            return result;
        }

        long deptStart = System.currentTimeMillis();
        ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIds(departmentIds);
        if(!departmentsResponseWrapper.isSucc()) {
            log.error("获取部门列表失败: {} 错误信息: {}", departmentIds, departmentsResponseWrapper.getMsg());
            return result;
        }
        List<DepartmentDTO> departments = departmentsResponseWrapper.getContent();
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

        log.info("查询条件 - 部门IDs: {}, 开始日期: {}, 结束日期: {}", departmentIds, startDate, endDate);

        // 1. 获取文件详情列表
        long fileDetailStart = System.currentTimeMillis();
        log.info("开始获取文件详情列表");
        List<SourceCodeFileDetailVO> fileDetails = sourceCodeReviewResultMapper.getStatisticsFileDetails(
                departmentIds, startDate, endDate);
        long fileDetailEnd = System.currentTimeMillis();
        log.info("getStatisticsFileDetails耗时: {} ms", (fileDetailEnd - fileDetailStart));
        log.info("获取到文件详情数量: {}", fileDetails.size());
        try {
            log.info("获取到文件详情: {}", objectMapper.writeValueAsString(fileDetails));
        } catch (JsonProcessingException e) {
            log.error("JSON转换失败", e);
        }

        result.setFileDetailVOList(fileDetails);

        // 2. 计算汇总数据
        long summaryStart = System.currentTimeMillis();
        log.info("开始计算汇总数据");
        int fileCount = fileDetails.size();
        int totalReviewPointCount = 0;
        int totalPassReviewPointCount = 0;

        for (SourceCodeFileDetailVO fileDetail : fileDetails) {
            if (fileDetail.getCheckPoints() != null) {
                totalReviewPointCount += fileDetail.getCheckPoints();
            }
            if (fileDetail.getPassCheckPoints() != null) {
                totalPassReviewPointCount += fileDetail.getPassCheckPoints();
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
        result.setTotalPassRate(totalPassRate);

        long summaryEnd = System.currentTimeMillis();
        log.info("汇总数据计算耗时: {} ms", (summaryEnd - summaryStart));
        log.info("汇总数据 - 文件数量: {}, 审查点数: {}, 通过审查点数: {}, 通过率: {}%",
                fileCount, totalReviewPointCount, totalPassReviewPointCount, totalPassRate);

        // 3. 构建图表数据
        long chartStart = System.currentTimeMillis();
        log.info("开始构建图表数据");
        Map<String, Map<String, Map<String, Object>>> chartDataMap = new LinkedHashMap<>();

        // 3.1 按部门统计（保留原有方式）
        long deptStatStart = System.currentTimeMillis();
        log.info("开始按部门统计");
        List<Map<String, Object>> departmentStats = sourceCodeReviewResultMapper.getStatisticsByDepartment(
                departmentIds, startDate, endDate);
        long deptStatEnd = System.currentTimeMillis();
        log.info("getStatisticsByDepartment耗时: {} ms", (deptStatEnd - deptStatStart));
        Map<String, Map<String, Object>> departmentChartData = buildChartData(departmentStats, "department_name");

        // 3.1.1 按部门统计用户数量（保留原有方式）
        long userCountStart = System.currentTimeMillis();
        List<Map<String, Object>> userCountByDepartment =
                sourceCodeReviewResultMapper.getUserCountByDepartment(departmentIds, null);
        long userCountEnd = System.currentTimeMillis();
        log.info("getUserCountByDepartment耗时: {} ms", (userCountEnd - userCountStart));
        Map<String, Object> deptUserCountMap = new HashMap<>();
        for (Map<String, Object> map : userCountByDepartment) {
            if (map.containsKey("department_name")) {
                deptUserCountMap.put(map.get("department_name").toString(), map.get("user_count"));
            }
        }
        departmentChartData.put("用户数量", deptUserCountMap);
        for (DepartmentDTO department : departments) {
            for (Map<String, Object> objectMap : departmentChartData.values()) {
                if(!objectMap.containsKey(department.getName())) {
                    objectMap.put(department.getName(), 0);
                }
            }
        }
        chartDataMap.put("按单位", departmentChartData);
        log.info("按部门统计完成，部门数量: {}", departmentStats.size());

        // 3.2-3.6 一次性获取所有时间维度数据
        long timeStatStart = System.currentTimeMillis();
        log.info("开始一次性获取所有时间维度数据");
        List<Map<String, Object>> timeStats = sourceCodeReviewResultMapper.getStatisticsByTimeDimensions(
                departmentIds, startDate, endDate);
        long timeStatEnd = System.currentTimeMillis();
        log.info("getStatisticsByTimeDimensions耗时: {} ms", (timeStatEnd - timeStatStart));

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

        // 3.7 按规则类型统计（保留原有方式）
        long ruleTypeStatStart = System.currentTimeMillis();
        log.info("开始按规则类型统计");
        List<Map<String, Object>> ruleTypeStats = sourceCodeReviewResultMapper.getStatisticsByRuleType(
                departmentIds, startDate, endDate);
        long ruleTypeStatEnd = System.currentTimeMillis();
        log.info("getStatisticsByRuleType耗时: {} ms", (ruleTypeStatEnd - ruleTypeStatStart));
        Map<String, Map<String, Object>> ruleTypeChartData = buildChartData(ruleTypeStats, "rule_type");
        chartDataMap.put("按规则类型", ruleTypeChartData);
        log.info("按规则类型统计完成，规则类型数: {}", ruleTypeStats.size());

        result.setChartDataMap(chartDataMap);

        long chartEnd = System.currentTimeMillis();
        log.info("图表数据构建耗时: {} ms", (chartEnd - chartStart));
        log.info("统计分析页面数据生成完成");
        long totalEnd = System.currentTimeMillis();
        log.info("getStatisticsPageData总耗时: {} ms", (totalEnd - totalStart));
        return result;
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
        String[] metricKeys = {"file_count", "pass_file_count", "rule_count"};
        String avgKey = "average_pass_rate";

        // 初始化指标Map
        for (String metricKey : metricKeys) {
            chartData.put(getMetricDisplayName(metricKey), new HashMap<>());
        }
        chartData.put(getMetricDisplayName(avgKey), new HashMap<>());

        // 临时存储用于加权平均的分子分母
        Map<String, Long> totalRuleCountMap = new HashMap<>();
        Map<String, Long> passRuleCountMap = new HashMap<>();

        // 累加各项数值
        for (Map<String, Object> stat : stats) {
            String dimensionValue = String.valueOf(stat.get(dimensionKey));
            if (dimensionValue != null && !"null".equals(dimensionValue)) {
                for (String metricKey : metricKeys) {
                    Object value = stat.get(metricKey);
                    if (value != null) {
                        Map<String, Object> metricMap = chartData.get(getMetricDisplayName(metricKey));
                        long oldValue = metricMap.getOrDefault(dimensionValue, 0L) instanceof Number ? ((Number)metricMap.getOrDefault(dimensionValue, 0L)).longValue() : 0L;
                        metricMap.put(dimensionValue, oldValue + ((Number)value).longValue());
                        // 记录用于平均通过率的分子分母
                        if ("file_count".equals(metricKey)) {
                            totalRuleCountMap.put(dimensionValue, totalRuleCountMap.getOrDefault(dimensionValue, 0L) + ((Number)value).longValue());
                        }
                        if ("pass_file_count".equals(metricKey)) {
                            passRuleCountMap.put(dimensionValue, passRuleCountMap.getOrDefault(dimensionValue, 0L) + ((Number)value).longValue());
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
            case "file_count" -> "文件数量";
            case "pass_file_count" -> "通过审查文件数";
            case "rule_count" -> "审查规则数";
            case "closed_loop_count" -> "已关闭数量";
            case "non_closed_loop_count" -> "未关闭数量";
            case "average_pass_rate" -> "平均通过率(%)";
            default -> metricKey;
        };
    }



    @Override
    public List<SourceCodeReviewStatisticsExportDataDTO> getStatisticsForExport(StatisticsExportRequestVO requestVO) {
        log.info("开始执行统计导出，请求参数: {}", requestVO);
        long startTime = System.currentTimeMillis();

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

        // 调用Mapper方法获取统计数据
        long dbStartTime = System.currentTimeMillis();
        List<SourceCodeReviewStatisticsExportDataDTO> exportDataList = sourceCodeReviewResultMapper.getStatisticsByDepartmentForExportExcel(startDate, endDate);

        long dbEndTime = System.currentTimeMillis();
        log.info("数据库查询完成，耗时: {}ms，查询结果数量: {}", (dbEndTime - dbStartTime), exportDataList.size());
        log.info("本地数据转换完成，转换数据数量: {}", exportDataList.size());

        if (deploymentType == 0) {
            try {
                log.info("当前为机载部署，需要远程获取631的统计数据");
                // 机载部署需要远程获取631的统计数据
                String targetURI = String.format("%s/sourcecodereview/v1/source-code-review-statistics/export-data", circuitServiceUrl);
                log.info("准备调用外部接口，URI: {}", targetURI);
                long externalStartTime = System.currentTimeMillis();
                List<SourceCodeReviewStatisticsExportDataDTO> statisticsForExportFromExternal = getStatisticsForExportFromExternal(requestVO, targetURI);
                long externalEndTime = System.currentTimeMillis();
                log.info("外部接口调用完成，耗时: {}ms，获取数据数量: {}", (externalEndTime - externalStartTime), statisticsForExportFromExternal.size());
                exportDataList.addAll(statisticsForExportFromExternal);
            } catch (Exception e) {
                log.error("远程获取631的统计数据失败: {}", e.getMessage(), e);
            }
        }
        else {
            log.info("当前为非机载部署，跳过外部接口调用，部署类型: {}", deploymentType);
        }

        long endTime = System.currentTimeMillis();
        log.info("统计导出完成，总耗时: {}ms，最终数据数量: {}", (endTime - startTime), exportDataList.size());
        return exportDataList;
    }


    @SneakyThrows
    public List<SourceCodeReviewStatisticsExportDataDTO> getStatisticsForExportFromExternal(StatisticsExportRequestVO requestVO, String targetURI) {
        HttpServletRequest request = HttpRequestUtil.getRequest();
        String authorization = request.getHeader("Authorization");
        String responseStr = httpUtilsService.postJsonString(objectMapper.writeValueAsString(requestVO), targetURI, authorization);
        log.info("请求631部署数据:{}", responseStr);

        // 解析响应字符串为ResponseWrapper<List<CircuitReviewStatisticsExportDataDTO>>
        ResponseWrapper<List<SourceCodeReviewStatisticsExportDataDTO>> responseWrapper =
                objectMapper.readValue(responseStr,
                        objectMapper.getTypeFactory()
                                .constructType(new TypeReference<ResponseWrapper<List<SourceCodeReviewStatisticsExportDataDTO>>>() {}.getType()));

        // 判断接口调用是否成功
        if (responseWrapper.isSucc()) {
            // 接口调用成功，返回content
            return responseWrapper.getContent();
        } else {
            // 接口调用失败，记录错误日志，返回空list
            log.error("请求631部署数据失败，错误信息: {}", responseWrapper.getMsg());
            return Collections.emptyList();
        }
    }


    public List<SourceCodeFileDetailVO> getStatisticsFileDetailsByLatestVersion(SourceCodeReviewStatisticsRequestVO requestVO) {
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

        log.info("查询条件 - 部门IDs: {}, 开始日期: {}, 结束日期: {}", departmentIds, startDate, endDate);

        // 1. 获取文件详情列表（基于最新版本）
        long fileDetailStart = System.currentTimeMillis();
        log.info("开始获取文件详情列表（基于最新版本）");
        return sourceCodeReviewResultMapper.getStatisticsFileDetails(
                departmentIds,  startDate, endDate);
    }

} 