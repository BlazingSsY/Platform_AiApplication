package com.starmol.circuitreview.backend.service.circuitreview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.DepartmentDTO;
import com.starmol.circuitreview.backend.bean.dto.UserDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewHomeStatisticsDataItemVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewHomeStatisticsDataVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.common.UserMetaData;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.repository.circuitreview.HomeStatisticsCircuitReviewResultMapper;
import com.starmol.circuitreview.backend.service.circuitreview.feign.UserServiceClient;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeStatisticsService {
    private final UserServiceClient userServiceClient;
    private final HomeStatisticsCircuitReviewResultMapper circuitReviewResultMapper;
    private final ObjectMapper objectMapper;

    public CircuitReviewHomeStatisticsDataVO getHomeStatistics() {
        // 获取当前用户信息
        long userStart = System.currentTimeMillis();
        UserMetaData currentUser = HttpRequestUtil.getUser();
        long userEnd = System.currentTimeMillis();
        log.info("获取当前用户信息耗时: {} ms", (userEnd - userStart));
        if (currentUser == null) {
            log.warn("获取当前用户信息失败");
            return new CircuitReviewHomeStatisticsDataVO();
        }

        long userByIdStart = System.currentTimeMillis();
        long userByIdEnd = System.currentTimeMillis();
        log.info("userService.getById耗时: {} ms", (userByIdEnd - userByIdStart));
        if (currentUser == null) {
            log.warn("用户不存在: {}", currentUser.getId());
            return new CircuitReviewHomeStatisticsDataVO();
        }

        long roleStart = System.currentTimeMillis();
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        long roleEnd = System.currentTimeMillis();
        log.info("roleService.getSysRoleTypeByUserId耗时: {} ms", (roleEnd - roleStart));
        if (userRole == null) {
            log.warn("用户没有角色: {}", currentUser.getId());
            return new CircuitReviewHomeStatisticsDataVO();
        }

        // 确定查询范围
        // 检查用户角色
        boolean isNormalUser = SysRoleTypeEnum.NORMAL_USER.equals(userRole) || SysRoleTypeEnum.EXPERT.equals(userRole);
        boolean isDepartmentSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);
        List<DepartmentDTO> departments;
        CircuitReviewHomeStatisticsDataVO result;
        if (isNormalUser) {
            // 普通用户：只查看自己上传和审查的文件
            log.info("获取用户当前部门信息，部门ID: {}", currentUser.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(currentUser.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentsResponseWrapper.getMsg());
                departments = new ArrayList<>();
            }
            departments = departmentsResponseWrapper.getContent();
            result = getCircuitReviewHomeStatisticsDataVOForNormalUser(currentUser.getId(), departments);
        } else if (isDepartmentSupervisor) {
            // 部门领导：查看全部用户数不为0的部门（包括子部门）的文件
            log.info("获取部门及其子部门ID列表，部门ID: {}", currentUser.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(currentUser.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentsResponseWrapper.getMsg());
                departments = new ArrayList<>();
            }
            departments = departmentsResponseWrapper.getContent();
            result = getCircuitReviewHomeStatisticsDataVOForDepartmentAdmin(departments);
        } else {
            // jz领导和admin：查看全部用户数不为0的部门（包括子部门）的文件
            log.info("获取部门及其子部门ID列表，部门ID: {}", currentUser.getDepartmentId());
            ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(currentUser.getDepartmentId());
            if(!departmentsResponseWrapper.isSucc()) {
                log.error("获取部门及其子部门列表失败: {} 错误信息: {}", currentUser.getDepartmentId(), departmentsResponseWrapper.getMsg());
                departments = new ArrayList<>();
            }
            departments = departmentsResponseWrapper.getContent();

            // 对部门名称进行中文拼音排序
            Collator collator = Collator.getInstance(Locale.CHINESE);
            departments.sort((d1, d2) -> collator.compare(d1.getName(), d2.getName()));
            result = getCircuitReviewHomeStatisticsDataVO(departments);
        }
        return sortResultMaps(result);
    }

    private CircuitReviewHomeStatisticsDataVO getCircuitReviewHomeStatisticsDataVOForNormalUser(Long userId, List<DepartmentDTO> departments) {
        List<Long> departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
        CircuitReviewHomeStatisticsDataVO result = new CircuitReviewHomeStatisticsDataVO();
        long totalStart = System.currentTimeMillis();

        // 获取最近审查的5个文件
        long recentStart = System.currentTimeMillis();
        log.info("开始获取最近审查的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> recentlyReviewedFiles =
                circuitReviewResultMapper.getRecentlyReviewedFiles(departmentIds, userId, 5);
        long recentEnd = System.currentTimeMillis();
        log.info("getRecentlyReviewedFiles耗时: {} ms", (recentEnd - recentStart));

        // 获取通过率最高的5个文件
        long passStart = System.currentTimeMillis();
        log.info("开始获取通过率最高的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> highestPassRateFiles =
                circuitReviewResultMapper.getHighestPassRateFiles(departmentIds, userId, 5);
        long passEnd = System.currentTimeMillis();
        log.info("getHighestPassRateFiles耗时: {} ms", (passEnd - passStart));

        long fileCountStart = System.currentTimeMillis();
        log.info("开始获取按用户统计的前12个月全部文件数量/审查文件数量");
        DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> yearMonthList = new LinkedList<>();
        YearMonth current = YearMonth.now();
        YearMonth start = current.minusMonths(11);
        for (int i = 0; i < 12; i++) {
            yearMonthList.add(current.format(yearMonthFormatter));
            current = current.minusMonths(1);
        }

        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<Map<String, Object>> filesCountByNormalUser = circuitReviewResultMapper.getFilesCountByNormalUser(
                userId, start.format(yearMonthFormatter) + "-01 00:00:00", endTime);
        List<Map<String, Object>> reviewedFilesCountByNormalUser = circuitReviewResultMapper.getReviewedFilesCountByNormalUser(
                userId, start.format(yearMonthFormatter) + "-01 00:00:00", endTime);
        long fileCountEnd = System.currentTimeMillis();
        log.info("getFilesCountByNormalUser耗时: {} ms", (fileCountEnd - fileCountStart));

        long filePassRateStart = System.currentTimeMillis();
        log.info("开始获取按用户文件通过率数据");
        List<Map<String, Object>> reviewFilePassRateByNormalUser = circuitReviewResultMapper.getReviewFilePassRateByNormalUser(userId);
        long filePassRateEnd = System.currentTimeMillis();
        log.info("getReviewFilePassRateByNormalUser耗时: {} ms", (filePassRateEnd - filePassRateStart));

        long failedRuleStart = System.currentTimeMillis();
        log.info("开始获取按用户失败的规则数量");
        List<Map<String, Object>> failedRuleCountByNormalUser = circuitReviewResultMapper.getFailedRuleCountByNormalUser(userId);
        long failedRuleEnd = System.currentTimeMillis();
        log.info("getFailedRuleCountByNormalUser耗时: {} ms", (failedRuleEnd - failedRuleStart));

        // 转换统计结果为Map格式
        long mapStart = System.currentTimeMillis();
        Map<String, Integer> totalFilesCountMap = new LinkedHashMap<>();
        Map<String, Integer> reviewedFilesCountMap = new LinkedHashMap<>();
        Map<String, BigDecimal> filesPassRateMap = new LinkedHashMap<>();
        Map<String, BigDecimal> filesPassRateStringMap = new LinkedHashMap<>();
        Map<String, Integer> failedRuleCountMap = new LinkedHashMap<>();

        for (String yearMonthStr : yearMonthList) {
            totalFilesCountMap.put(yearMonthStr, 0);
            reviewedFilesCountMap.put(yearMonthStr, 0);
        }

        // 处理全部文件数量统计
        for (Map<String, Object> item : filesCountByNormalUser) {
            String yearMonth = (String) item.get("create_month");
            Integer totalFileCount = ((Number) item.get("file_count")).intValue();
            if (yearMonth != null) {
                totalFilesCountMap.put(yearMonth, totalFileCount);
            }
        }
        // 处理审查文件数量统计
        for (Map<String, Object> item : reviewedFilesCountByNormalUser) {
            String yearMonth = (String) item.get("review_month");
            Integer fileCountReviewed = ((Number) item.get("file_count_reviewed")).intValue();
            if (yearMonth != null) {
                reviewedFilesCountMap.put(yearMonth, fileCountReviewed);
            }
        }
        // 处理审查文件通过率统计
        for (Map<String, Object> item : reviewFilePassRateByNormalUser) {
            String fileName = (String) item.get("file_name");
            BigDecimal passRate = (BigDecimal) item.get("pass_rate");
            if (fileName != null) {
                filesPassRateMap.put(fileName, passRate);
                filesPassRateStringMap.put(fileName, passRate);
            }
        }
        // 处理失败规则统计
        for (Map<String, Object> item : failedRuleCountByNormalUser) {
            String ruleName = (String) item.get("rule_name");
            Integer ruleCount = ((Number) item.get("rule_count")).intValue();
            if (ruleName != null) {
                failedRuleCountMap.put(ruleName, ruleCount);
            }
        }


        result.setMostRecentlyReviewedFiles(recentlyReviewedFiles);
        result.setHighestPassRateFiles(highestPassRateFiles);
        result.setTotalFilesCountByDepartment(totalFilesCountMap);
        result.setReviewedFilesCountByDepartment(reviewedFilesCountMap);
        result.setUserCountByDepartment(filesPassRateStringMap);
        result.setReviewIssueCountByDepartment(failedRuleCountMap);

        long totalEnd = System.currentTimeMillis();
        log.info("getHomeStatistics总耗时: {} ms", (totalEnd - totalStart));
        return result;
    }

    private CircuitReviewHomeStatisticsDataVO getCircuitReviewHomeStatisticsDataVOForDepartmentAdmin(List<DepartmentDTO> departments) {
        List<Long> departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
        List<UserDTO> users;

        ResponseWrapper<List<UserDTO>> usersResponseWrapper = userServiceClient.getUsersByDepartmentIds(departmentIds);
        if(!usersResponseWrapper.isSucc()) {
            log.error("获取部门用户列表失败: {} 错误信息: {}", departmentIds, usersResponseWrapper.getMsg());
            users = new ArrayList<>();
        }
        users = usersResponseWrapper.getContent();

        // 对用户名称进行中文拼音排序
        Collator collator = Collator.getInstance(Locale.CHINESE);
        users.sort((u1, u2) -> collator.compare(u1.getName(), u2.getName()));
        CircuitReviewHomeStatisticsDataVO result = new CircuitReviewHomeStatisticsDataVO();
        long totalStart = System.currentTimeMillis();

        // 获取最近审查的5个文件
        long recentStart = System.currentTimeMillis();
        log.info("开始获取最近审查的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> recentlyReviewedFiles =
                circuitReviewResultMapper.getRecentlyReviewedFiles(departmentIds, null, 5);
        long recentEnd = System.currentTimeMillis();
        log.info("getRecentlyReviewedFiles耗时: {} ms", (recentEnd - recentStart));

        // 获取通过率最高的5个文件
        long passStart = System.currentTimeMillis();
        log.info("开始获取通过率最高的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> highestPassRateFiles =
                circuitReviewResultMapper.getHighestPassRateFiles(departmentIds, null, 5);
        long passEnd = System.currentTimeMillis();
        log.info("getHighestPassRateFiles耗时: {} ms", (passEnd - passStart));

        // 获取按部门统计的全部文件数量
        long totalFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门用户统计的全部文件数量");
        List<Map<String, Object>> totalFilesCountByDepartmentAdmin =
                circuitReviewResultMapper.getTotalFilesCountByDepartmentAdmin(departmentIds);
        long totalFilesEnd = System.currentTimeMillis();
        log.info("getTotalFilesCountByDepartment耗时: {} ms", (totalFilesEnd - totalFilesStart));

        // 获取按部门统计的审查文件数量
        long reviewedFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门用户统计的审查文件数量");
        List<Map<String, Object>> reviewedFilesCountByDepartmentAdmin =
                circuitReviewResultMapper.getReviewedFilesCountByDepartmentAdmin(departmentIds);
        long reviewedFilesEnd = System.currentTimeMillis();
        log.info("getReviewedFilesCountByDepartment耗时: {} ms", (reviewedFilesEnd - reviewedFilesStart));

        // 获取按部门统计的用户数量
        long userCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的用户数量");
        List<Map<String, Object>> userCountByDepartment =
                circuitReviewResultMapper.getUserCountByDepartment(departmentIds, null);
        long userCountEnd = System.currentTimeMillis();
        log.info("getUserCountByDepartment耗时: {} ms", (userCountEnd - userCountStart));

        // 获取按部门统计的审核不通过规则数量
        long issueCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的审核不通过规则数量");
        List<Map<String, Object>> reviewIssueCountByDepartmentAdmin =
                circuitReviewResultMapper.getReviewIssueCountByDepartmentAdmin(departmentIds);
        long issueCountEnd = System.currentTimeMillis();
        log.info("getReviewIssueCountByDepartment耗时: {} ms", (issueCountEnd - issueCountStart));

        // 转换统计结果为Map格式
        long mapStart = System.currentTimeMillis();
        Map<String, Integer> totalFilesCountMap = new LinkedHashMap<>();
        Map<String, Integer> reviewedFilesCountMap = new LinkedHashMap<>();
        Map<String, BigDecimal> userCountMap = new HashMap<>();
        Map<String, Integer> reviewIssueCountMap = new LinkedHashMap<>();

        for (UserDTO user : users) {
            totalFilesCountMap.put(user.getName(), 0);
            reviewedFilesCountMap.put(user.getName(), 0);
            reviewIssueCountMap.put(user.getName(), 0);
        }

        // 处理全部文件数量统计
        for (Map<String, Object> item : totalFilesCountByDepartmentAdmin) {
            String userName = (String) item.get("user_name");
            Integer fileCount = ((Number) item.get("file_count")).intValue();
            if (userName != null) {
                totalFilesCountMap.put(userName, fileCount);
            }
        }

        // 处理审查文件数量统计
        for (Map<String, Object> item : reviewedFilesCountByDepartmentAdmin) {
            String userName = (String) item.get("user_name");
            Integer fileCount = ((Number) item.get("file_count")).intValue();
            if (userName != null) {
                reviewedFilesCountMap.put(userName, fileCount);
            }
        }

        // 处理审核不通过规则数量统计
        for (Map<String, Object> item : reviewIssueCountByDepartmentAdmin) {
            String userName = (String) item.get("user_name");
            Integer issueCount = ((Number) item.get("issue_count")).intValue();
            if (userName != null) {
                reviewIssueCountMap.put(userName, issueCount);
            }
        }

        long mapEnd = System.currentTimeMillis();
        log.info("Map数据处理耗时: {} ms", (mapEnd - mapStart));

        // 处理用户数量统计
        long userMapStart = System.currentTimeMillis();
        // 部门负责人或其他角色：显示实际用户数量
        for (Map<String, Object> item : userCountByDepartment) {
            String departmentName = (String) item.get("department_name");
            Integer userCount = ((Number) item.get("user_count")).intValue();
            if (departmentName != null) {
                userCountMap.put(departmentName, BigDecimal.valueOf(userCount));
            }
        }
        departments.stream().filter(department -> !userCountMap.containsKey(department.getName())).toList().forEach(department -> {
            userCountMap.put(department.getName(), BigDecimal.ZERO);
        });
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

    @NotNull
    private CircuitReviewHomeStatisticsDataVO getCircuitReviewHomeStatisticsDataVO(List<DepartmentDTO> departments) {
        List<Long> departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
        CircuitReviewHomeStatisticsDataVO result = new CircuitReviewHomeStatisticsDataVO();
        long totalStart = System.currentTimeMillis();

        // 获取最近审查的5个文件
        long recentStart = System.currentTimeMillis();
        log.info("开始获取最近审查的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> recentlyReviewedFiles =
                circuitReviewResultMapper.getRecentlyReviewedFiles(departmentIds, null, 5);
        long recentEnd = System.currentTimeMillis();
        log.info("getRecentlyReviewedFiles耗时: {} ms", (recentEnd - recentStart));

        // 获取通过率最高的5个文件
        long passStart = System.currentTimeMillis();
        log.info("开始获取通过率最高的5个文件");
        List<CircuitReviewHomeStatisticsDataItemVO> highestPassRateFiles =
                circuitReviewResultMapper.getHighestPassRateFiles(departmentIds, null, 5);
        long passEnd = System.currentTimeMillis();
        log.info("getHighestPassRateFiles耗时: {} ms", (passEnd - passStart));

        // 获取按部门统计的全部文件数量
        long totalFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的全部文件数量");
        List<Map<String, Object>> totalFilesCountByDepartment =
                circuitReviewResultMapper.getTotalFilesCountByDepartment(departmentIds, null);
        long totalFilesEnd = System.currentTimeMillis();
        log.info("getTotalFilesCountByDepartment耗时: {} ms", (totalFilesEnd - totalFilesStart));

        // 获取按部门统计的审查文件数量
        long reviewedFilesStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的审查文件数量");
        List<Map<String, Object>> reviewedFilesCountByDepartment =
                circuitReviewResultMapper.getReviewedFilesCountByDepartment(departmentIds, null);
        long reviewedFilesEnd = System.currentTimeMillis();
        log.info("getReviewedFilesCountByDepartment耗时: {} ms", (reviewedFilesEnd - reviewedFilesStart));

        // 获取按部门统计的用户数量
        long userCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的用户数量");
        List<Map<String, Object>> userCountByDepartment =
                circuitReviewResultMapper.getUserCountByDepartment(departmentIds, null);
        long userCountEnd = System.currentTimeMillis();
        log.info("getUserCountByDepartment耗时: {} ms", (userCountEnd - userCountStart));

        // 获取按部门统计的审核不通过规则数量
        long issueCountStart = System.currentTimeMillis();
        log.info("开始获取按部门统计的审核不通过规则数量");
        List<Map<String, Object>> reviewIssueCountByDepartment =
                circuitReviewResultMapper.getReviewIssueCountByDepartment(departmentIds, null);
        long issueCountEnd = System.currentTimeMillis();
        log.info("getReviewIssueCountByDepartment耗时: {} ms", (issueCountEnd - issueCountStart));

        // 转换统计结果为Map格式
        long mapStart = System.currentTimeMillis();
        Map<String, Integer> totalFilesCountMap = new LinkedHashMap<>();
        Map<String, Integer> reviewedFilesCountMap = new LinkedHashMap<>();
        Map<String, BigDecimal> userCountMap = new LinkedHashMap<>();
        Map<String, Integer> reviewIssueCountMap = new LinkedHashMap<>();

        if (CollectionUtils.isNotEmpty(departments)) {
            for (DepartmentDTO department : departments) {
                totalFilesCountMap.put(department.getName(), 0);
                reviewedFilesCountMap.put(department.getName(), 0);
                reviewIssueCountMap.put(department.getName(), 0);
                userCountMap.put(department.getName(), BigDecimal.ZERO);
            }

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
        }

        long mapEnd = System.currentTimeMillis();
        log.info("Map数据处理耗时: {} ms", (mapEnd - mapStart));

        // 处理用户数量统计
        long userMapStart = System.currentTimeMillis();
        // 部门负责人或其他角色：显示实际用户数量
        for (Map<String, Object> item : userCountByDepartment) {
            String departmentName = (String) item.get("department_name");
            Integer userCount = ((Number) item.get("user_count")).intValue();
            if (departmentName != null) {
                userCountMap.put(departmentName, BigDecimal.valueOf(userCount));
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

    /**
     * 对结果对象中的Map进行排序，按值从大到小排序
     */
    private CircuitReviewHomeStatisticsDataVO sortResultMaps(CircuitReviewHomeStatisticsDataVO result) {
        // 对totalFilesCountByDepartment按值从大到小排序
        Map<String, Integer> sortedTotalFilesCountByDepartment = result.getTotalFilesCountByDepartment().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 对reviewedFilesCountByDepartment按值从大到小排序
        Map<String, Integer> sortedReviewedFilesCountByDepartment = result.getReviewedFilesCountByDepartment().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 对userCountByDepartment按值从大到小排序（值是字符串形式的数字）
        Map<String, BigDecimal> sortedUserCountByDepartment = result.getUserCountByDepartment().entrySet()
                .stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 对reviewIssueCountByDepartment按值从大到小排序
        Map<String, Integer> sortedReviewIssueCountByDepartment = result.getReviewIssueCountByDepartment().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // 设置排序后的Map
        result.setTotalFilesCountByDepartment(sortedTotalFilesCountByDepartment);
        result.setReviewedFilesCountByDepartment(sortedReviewedFilesCountByDepartment);
        result.setUserCountByDepartment(sortedUserCountByDepartment);
        result.setReviewIssueCountByDepartment(sortedReviewIssueCountByDepartment);

        try {
            log.info("排序后的首页结果: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
