package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.UserDTO;
import com.starmol.circuitreview.backend.bean.vo.*;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.common.UserMetaData;
import com.starmol.circuitreview.backend.constant.*;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.circuitreview.*;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewResultMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.*;
import com.starmol.circuitreview.backend.service.circuitreview.feign.ReviewClient;
import com.starmol.circuitreview.backend.service.circuitreview.feign.UserServiceClient;
import com.starmol.circuitreview.backend.service.common.StorageService;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import com.starmol.circuitreview.backend.utils.IdWorker;
import com.starmol.circuitreview.backend.utils.MultipartFileUtils;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 电路审查结果服务实现类
 *
 * @author system
 * @since 2025-01-07
 */

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class CircuitReviewResultServiceImpl extends BaseServiceImpl<CircuitReviewResultMapper, CircuitReviewResult> implements CircuitReviewResultService {

    private final ObjectMapper objectMapper;
    private final ReviewClient reviewClient;
    private final UserServiceClient userServiceClient;
    private final CircuitReviewRuleService circuitReviewRuleService;
    private final CircuitReviewResultDetailService circuitReviewResultDetailService;
    private final CircuitFileService circuitFileService;
    private final CircuitReviewIssueService circuitReviewIssueService;
    private final StorageService storageService;
    private final CircuitReviewResultAuditService circuitReviewResultAuditService;
    private final CircuitReviewResultDetailAuditService circuitReviewResultDetailAuditService;

    @PostConstruct
    public void init() {
        // 创建新线程实现异步延迟执行，避免 Bean 初始化时序问题
        new Thread(() -> {
            try {
                log.info("开始延迟任务，30 秒后将执行历史审查问题数据补全");
                Thread.sleep(30000); // 延迟 30 秒 (30000 毫秒)
                log.info("延迟任务完成，开始执行历史审查问题数据补全");
                populateResultIssues();
                initializeMaxCheckPoints();
            } catch (InterruptedException e) {
                log.error("延迟任务被中断", e);
                Thread.currentThread().interrupt();
            }
        }, "CircuitReviewIssue-Populate-Thread").start();
        
        log.info("CircuitReviewResultServiceImpl 初始化完成，已启动后台任务线程");
    }

    /**
     * 历史审查问题数据补全方法
     * 查询所有需要补全的审查结果，创建对应的 CircuitReviewIssue 记录并更新统计信息
     */
    private void populateResultIssues() {
        log.info("开始补全历史审查问题的 CircuitReviewIssue 记录");

        // 查询所有已存在 Issue 的 fileId
        List<Long> existingFileIds = circuitReviewIssueService.list(Wrappers.<CircuitReviewIssue>lambdaQuery()
                        .select(CircuitReviewIssue::getFileId))
                .stream()
                .map(CircuitReviewIssue::getFileId)
                .distinct()
                .toList();

        // 查询所有 status 为 FINISHED 且 fileId 不在已存在列表中的 CircuitReviewResult
        LambdaQueryWrapper<CircuitReviewResult> queryWrapper = Wrappers.<CircuitReviewResult>lambdaQuery()
                .eq(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FINISHED);

        if (CollectionUtils.isNotEmpty(existingFileIds)) {
            queryWrapper.notIn(CircuitReviewResult::getFileId, existingFileIds);
        }

        List<CircuitReviewResult> resultsNeedIssues = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(resultsNeedIssues)) {
            log.info("无需补全的历史审查问题记录");
            return;
        }

        log.info("找到 {} 个需要补全 Issue 的审查结果", resultsNeedIssues.size());

        // 按 fileId 分组，组内按 fileVersionId 升序排列
        Map<Long, List<CircuitReviewResult>> resultsByFileIdMap = resultsNeedIssues.stream()
                .collect(Collectors.groupingBy(
                        CircuitReviewResult::getFileId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(CircuitReviewResult::getFileVersionId))
                                        .toList()
                        )
                ));

        int totalCreated = 0;
        int totalUpdated = 0;

        // 更新该文件所有审查结果的 totalFailCheckPoints 和 closedFailCheckPoints
        List<CircuitReviewResult> resultsToUpdate = new ArrayList<>();

        for (Map.Entry<Long, List<CircuitReviewResult>> entry : resultsByFileIdMap.entrySet()) {
            Long fileId = entry.getKey();
            List<CircuitReviewResult> resultsForFile = entry.getValue();

            try {
                // 为该文件的每个审查结果创建 CircuitReviewIssue
                for (CircuitReviewResult result : resultsForFile) {
                    // 获取该审查结果的所有详情
                    List<CircuitReviewResultDetail> details = circuitReviewResultDetailService.list(
                            Wrappers.<CircuitReviewResultDetail>lambdaQuery()
                                    .eq(CircuitReviewResultDetail::getResultId, result.getId())
                    );

                    if (CollectionUtils.isEmpty(details)) {
                        log.warn("审查结果 ID: {} 没有对应的详情记录", result.getId());
                        continue;
                    }

                    // 根据详情创建 CircuitReviewIssue
                    List<CircuitReviewIssue> issues = new ArrayList<>();
                    for (CircuitReviewResultDetail detail : details) {
                        // 只处理未通过的详情（与 asyncReview 逻辑保持一致）
                        if (detail.getIsPassed() != null && detail.getIsPassed() == 0) {
                            if (detail.getRuleId() == null && detail.getRuleCode() == null) {
                                continue;
                            } else if (detail.getRuleId() == null) {
                                CircuitReviewRuleVO ruleVOByCode = circuitReviewRuleService.getCircuitReviewRuleVOByCode(detail.getRuleCode());
                                if (ruleVOByCode != null) {
                                    detail.setRuleId(ruleVOByCode.getId());
                                }

                            } else if (detail.getRuleCode() == null) {
                                CircuitReviewRuleVO ruleVOById = circuitReviewRuleService.getCircuitReviewRuleVOById(detail.getRuleId());
                                if (ruleVOById != null) {
                                    detail.setRuleCode(ruleVOById.getCode());
                                }
                            }
                            if (StringUtils.isNotEmpty(detail.getTagPin())) {
                                // 如果存在引脚标签，则根据逗号分隔的引脚数量来创建多个问题
                                String[] tagPinList = detail.getTagPin().split(",");
                                for (String tagPin : tagPinList) {
                                    issues.add(new CircuitReviewIssue()
                                            .setFileId(result.getFileId())
                                            .setFileVersionId(result.getFileVersionId())
                                            .setResultId(result.getId())
                                            .setResultDetailId(detail.getId())
                                            .setRuleId(detail.getRuleId())
                                            .setRuleCode(detail.getRuleCode())
                                            .setDeviceType(detail.getDeviceType())
                                            .setTagPin(tagPin.trim())
                                            .setReviewSuggestion(detail.getReviewSuggestion())
                                            .setReviewTime(result.getReviewTime()));
                                }
                            } else {
                                // 不存在位号管脚则只创建一条电路审查问题
                                issues.add(new CircuitReviewIssue()
                                        .setFileId(result.getFileId())
                                        .setFileVersionId(result.getFileVersionId())
                                        .setResultId(result.getId())
                                        .setResultDetailId(detail.getId())
                                        .setRuleId(detail.getRuleId())
                                        .setRuleCode(detail.getRuleCode())
                                        .setDeviceType(detail.getDeviceType())
                                        .setReviewSuggestion(detail.getReviewSuggestion())
                                        .setReviewTime(result.getReviewTime()));
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(issues)) {
                        // 批量保存（自动去重）
                        circuitReviewIssueService.saveIssuesBatch(issues);
                        totalCreated += issues.size();
                        log.info("审查结果 ID: {}, 创建了 {} 个 Issue 记录", result.getId(), issues.size());
                    }

                    // 查询该文件存在的问题总数
                    Long fileReviewIssueCount = circuitReviewIssueService.getFileReviewIssueCount(fileId);
                    log.info("文件 ID: {} 共有 {} 个 Issue 记录", fileId, fileReviewIssueCount);

                    Integer checkPoints = result.getCheckPoints() != null ? result.getCheckPoints() : 0;
                    Integer passCheckPoints = result.getPassCheckPoints() != null ? result.getPassCheckPoints() : 0;
                    Integer currentFailedCount = checkPoints - passCheckPoints;

                    // 所有问题数量 = 该文件的 Issue 总数
                    Integer totalFailCheckPoints = fileReviewIssueCount.intValue();

                    // 关闭的问题数量 = 所有问题数量 - 当前失败的问题数量
                    int closedFailCheckPoints = totalFailCheckPoints - currentFailedCount;
                    if (closedFailCheckPoints < 0) {
                        closedFailCheckPoints = 0;
                    }

                    result.setTotalFailCheckPoints(totalFailCheckPoints);
                    result.setClosedFailCheckPoints(closedFailCheckPoints);
                    resultsToUpdate.add(result);
                }

                // 批量更新审查结果
                if (CollectionUtils.isNotEmpty(resultsToUpdate)) {
                    this.updateBatchById(resultsToUpdate);
                    totalUpdated += resultsToUpdate.size();
                    log.info("文件 ID: {} 更新了 {} 个审查结果的统计信息", fileId, resultsToUpdate.size());
                }

            } catch (Exception e) {
                log.error("处理文件 ID: {} 的审查结果时发生异常", fileId, e);
            }
        }

        log.info("历史审查问题记录补全完成，共创建 {} 条记录，更新了 {} 个审查结果的统计信息", totalCreated, totalUpdated);
    }

    /**
     * 初始化 CircuitFile 的 maxCheckPoints 字段
     * 查询所有 maxCheckPoints 为空的 CircuitFile，找到每个文件对应的最大 checkPoints 并更新
     */
    private void initializeMaxCheckPoints() {
        log.info("开始初始化 CircuitFile 的 maxCheckPoints 字段");

        // 1. 查询所有 maxCheckPoints 为空的 CircuitFile
        List<CircuitFile> filesNeedInit = circuitFileService.lambdaQuery()
                .isNull(CircuitFile::getMaxCheckPoints)
                .list();

        if (CollectionUtils.isEmpty(filesNeedInit)) {
            log.info("无需初始化 maxCheckPoints 的 CircuitFile 记录");
            return;
        }

        // 2. 获取这些文件的 ID 列表
        List<Long> fileIds = filesNeedInit.stream()
                .map(CircuitFile::getId)
                .distinct()
                .toList();

        log.info("找到 {} 个需要初始化 maxCheckPoints 的文件", fileIds.size());

        // 3. 查询每个 fileId 对应的最大 checkPoints
        List<Map<String, Object>> maxCheckPointsList = this.baseMapper.selectMaxCheckPointsByFileIds(fileIds);

        if (CollectionUtils.isEmpty(maxCheckPointsList)) {
            log.info("未找到任何审查结果记录，无需更新 maxCheckPoints");
            return;
        }

        // 4. 构建更新列表
        List<CircuitFile> filesToUpdate = new ArrayList<>();
        for (Map<String, Object> map : maxCheckPointsList) {
            Long fileId = ((Number) map.get("file_id")).longValue();
            Integer maxCheckPoints = map.get("max_check_points") != null ?
                    ((Number) map.get("max_check_points")).intValue() : 0;

            CircuitFile file = new CircuitFile();
            file.setId(fileId);
            file.setMaxCheckPoints(maxCheckPoints);
            filesToUpdate.add(file);
        }

        // 5. 批量更新 CircuitFile
        if (CollectionUtils.isNotEmpty(filesToUpdate)) {
            circuitFileService.updateBatchById(filesToUpdate);
            log.info("成功更新 {} 个 CircuitFile 的 maxCheckPoints", filesToUpdate.size());
        }

        log.info("CircuitFile 的 maxCheckPoints 字段初始化完成");
    }

    @Override
    public CircuitReviewResultVO createCircuitReviewResult(CircuitReviewResult circuitReviewResult) {
        CircuitReviewResult savedCircuitReviewResult = this.saveAndReturnObject(circuitReviewResult);
        return convertToVO(savedCircuitReviewResult);
    }

    @Override
    public void removeResultByFileId(Long fileId) {
        // 获取当前用户信息
        UserMetaData currentUser = HttpRequestUtil.getUser();
        if (currentUser == null) {
            throw new KnowException("获取当前用户信息失败");
        }
        // 获取用户角色
        SysRoleTypeEnum userRole = getSysRoleType();
        if ((userRole == null) || (!SysRoleTypeEnum.SUPER_SUPERVISOR.equals(userRole) && !SysRoleTypeEnum.ADMIN.equals(userRole))) {
            throw new KnowException("当前用户无权限删除审查记录");
        }
        log.info("删除电路审查结果，文件ID: {}", fileId);

        List<CircuitReviewResult> circuitReviewResults = this.list(
                Wrappers.<CircuitReviewResult>lambdaQuery()
                        .eq(CircuitReviewResult::getFileId, fileId)
        );
        List<Long> idList = circuitReviewResults.stream().map(CircuitReviewResult::getId).toList();
        if (CollectionUtils.isNotEmpty(idList)) {
            this.remove(Wrappers.<CircuitReviewResult>lambdaQuery().in(CircuitReviewResult::getId, idList));
            circuitReviewResultDetailService.removeDetailByResultIdList(idList);
        } else {
            log.info("无电路审查结果，文件ID: {}", fileId);
        }
    }

    @Override
    public CircuitReviewResultVO updateCircuitReviewResult(Long id, CircuitReviewResult circuitReviewResult) {
        circuitReviewResult.setId(id);
        CircuitReviewResult updatedCircuitReviewResult = this.updateByIDAndReturnObject(circuitReviewResult);
        return convertToVO(updatedCircuitReviewResult);
    }

    @Override
    public CircuitReviewResultVO getCircuitReviewResultVOById(Long id) {
        CircuitReviewResult circuitReviewResult = this.getById(id);
        return convertToVO(circuitReviewResult);
    }

    @Override
    public IPage<CircuitReviewResultVO> getCircuitReviewResultVOPage(Page<CircuitReviewResult> page, Long fileId) {
        IPage<CircuitReviewResult> circuitReviewResultPage = this.page(page,
                Wrappers.<CircuitReviewResult>lambdaQuery()
                        .eq(fileId != null, CircuitReviewResult::getFileId, fileId)
        );
        return circuitReviewResultPage.convert(this::convertToVO);
    }

    private Long submitCircuitReview(CircuitReviewRequestVO circuitReviewRequestVO, Long userId, String userName) {
        log.info("提交电路审查，文件ID: {}", circuitReviewRequestVO.getFileVersionId());
        // 获取文件
        CircuitFileVersionService circuitFileVersionService = SpringContextUtils.getInstanceByType(CircuitFileVersionService.class);
        CircuitFileVersionVO circuitFileVersionVO = circuitFileVersionService.getCircuitFileVersionVOByVersionId(circuitReviewRequestVO.getFileVersionId());
        if (Objects.nonNull(circuitFileVersionVO)) {
            CircuitReviewResult circuitReviewResult = new CircuitReviewResult()
                    .setFileId(circuitFileVersionVO.getFileId())
                    .setFileVersionId(circuitFileVersionVO.getId())
                    .setReviewTime(LocalDateTime.now())
                    .setStatus(CircuitReviewStatusEnum.IN_PROGRESS);
            circuitReviewResult.setId(IdWorker.getId());
            circuitReviewResult.setCreateUser(userId);
            circuitReviewResult.setCreateDate(LocalDateTime.now());
            this.save(circuitReviewResult);
            CircuitReviewResult saved = this.getById(circuitReviewResult.getId());

            try {
                // 从存储服务获取文件内容并创建MultipartFile
                MultipartFile multipartFile = MultipartFileUtils.createMultipartFileFromStorage(storageService, circuitFileVersionVO);

                // 调用第三方服务上传文件
                ResponseWrapper<Map<String, Object>> uploadResponse = reviewClient.uploadFile(multipartFile);
                if (!uploadResponse.isSucc()) {
                    // 上传失败，更新状态并抛出异常
                    CircuitReviewResult update = new CircuitReviewResult();
                    update.setId(saved.getId());
                    update.setStatus(CircuitReviewStatusEnum.FAILED);
                    update.setErrorMessage(uploadResponse.getMsg());
                    update.setUpdateUser(userId);
                    update.setUpdateDate(LocalDateTime.now());
                    this.updateById(update);
                    throw new KnowException("文件上传到第三方服务失败: " + uploadResponse.getMsg());
                }

                log.info("文件上传到第三方服务成功，文件ID: {} 文件版本ID: {}", circuitFileVersionVO.getFileId(), circuitFileVersionVO.getId());

                List<UserDTO> users = new ArrayList<>();
                try {
                    ResponseWrapper<List<UserDTO>> usersResponseWrapper = userServiceClient.getUsersByIds(List.of(userId));
                    if(!usersResponseWrapper.isSucc()) {
                        log.error("获取用户列表失败: {} 错误信息: {}", List.of(userId), usersResponseWrapper.getMsg());
                    }
                    users = usersResponseWrapper.getContent();

                } catch (Exception e) {
                    log.error("获取用户信息失败", e);
                }

                // 异步处理 - 通过ApplicationContext获取代理对象来调用异步方法
                CircuitReviewResultServiceImpl circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultServiceImpl.class);
                String departmentName = "Unknown";
                if(CollectionUtils.isNotEmpty(users)) {
                    departmentName = users.get(0).getDepartmentName();
                }
                circuitReviewResultService.asyncReview(saved, multipartFile.getOriginalFilename(), userId, circuitFileVersionVO, userName, departmentName);
                return saved.getId();
            } catch (Exception e) {
                log.error("文件上传到第三方服务失败", e);
                // 上传失败，更新状态并抛出异常
                CircuitReviewResult update = new CircuitReviewResult();
                update.setId(saved.getId());
                update.setStatus(CircuitReviewStatusEnum.FAILED);
                update.setErrorMessage(e.getMessage());
                update.setUpdateUser(userId);
                update.setUpdateDate(LocalDateTime.now());
                this.updateById(update);
                throw new KnowException("文件上传到第三方服务失败: " + e.getMessage());
            }
        } else {
            log.error("电路文件版本不存在: {}", circuitReviewRequestVO.getFileVersionId());
            throw new KnowException("电路文件版本不存在");
        }
    }

    @Override
    public Long submitCircuitReview(CircuitReviewRequestVO circuitReviewRequestVO) {
        return submitCircuitReview(circuitReviewRequestVO, HttpRequestUtil.getUserId(), HttpRequestUtil.getLoginName());
    }

    @Override
    public List<CircuitReviewRuleSummaryItemVO> getCircuitReviewRuleSummary(Long id) {
        CircuitReviewResult circuitReviewResult = this.getById(id);
        if (circuitReviewResult != null) {
            List<CircuitReviewRuleSummaryItemVO> summaryItemVOList = new ArrayList<>();
            if (circuitReviewResult.getStatus() == CircuitReviewStatusEnum.FINISHED) {
                List<CircuitReviewResultDetailVO> detailVOList = circuitReviewResultDetailService.getAllDetailsByResultId(id);
                List<CircuitReviewRuleVO> circuitReviewRuleVOList = circuitReviewRuleService.getCircuitReviewRuleVOList(null, null);
                circuitReviewRuleVOList.forEach(circuitReviewRuleVO -> {
                    CircuitReviewRuleSummaryItemVO itemVO = new CircuitReviewRuleSummaryItemVO();
                    itemVO.setRule(circuitReviewRuleVO);
                    List<CircuitReviewResultDetailVO> matchDetailList = detailVOList.stream().filter(detailVO -> detailVO.getRuleId().equals(circuitReviewRuleVO.getId())).toList();
                    if (CollectionUtils.isNotEmpty(matchDetailList)) {
                        Set<Integer> isPassedSet = matchDetailList.stream().map(CircuitReviewResultDetailVO::getIsPassed).collect(Collectors.toSet());
                        if (isPassedSet.contains(0)) {
                            itemVO.setSummary(CircuitReviewRuleSummaryEnum.NOT_PASSED);
                        } else {
                            itemVO.setSummary(CircuitReviewRuleSummaryEnum.PASSED);
                        }
                    } else {
                        itemVO.setSummary(CircuitReviewRuleSummaryEnum.PASSED);
                    }
                    summaryItemVOList.add(itemVO);
                });
            }
            return summaryItemVOList;
        } else {
            throw new KnowException("不存在该电路审查记录");
        }
    }

    @Override
    public CircuitReviewResultFilterVO getCircuitReviewResultFilters(Long id, Integer ruleType, String reviewRule, String deviceType, Integer isPassed) {
        CircuitReviewResultFilterVO filterVO = new CircuitReviewResultFilterVO();
        List<ReviewDetailRuleFilterDataVO> allData = circuitReviewResultDetailService.getCircuitReviewResultDetailFilters(id);
        Stream<ReviewDetailRuleFilterDataVO> stream = allData.stream();
        if (Objects.nonNull(ruleType)) {
            stream = stream.filter(filter -> filter.getRuleType() != null && filter.getRuleType().getValue().equals(ruleType));
        }
        if (StringUtils.isNotEmpty(reviewRule)) {
            stream = stream.filter(filter -> filter.getReviewRule().contains(reviewRule));
        }
        if (StringUtils.isNotEmpty(deviceType)) {
            stream = stream.filter(filter -> {
                if (deviceType.endsWith(" ")) {
                    boolean b = StringUtils.isBlank(filter.getDeviceType());
                    log.info("deviceType: {} result: {}", filter.getDeviceType(), b);
                    return b;
                } else {
                    return StringUtils.isNotBlank(filter.getDeviceType()) && filter.getDeviceType().contains(deviceType);
                }
            });
        }
        if (Objects.nonNull(isPassed)) {
            stream = stream.filter(filter -> filter.getIsPassed().equals(isPassed));
        }
        List<ReviewDetailRuleFilterDataVO> filteredData = stream.toList();
        filterVO.setRuleTypeFilters(filteredData.stream().filter(data -> Objects.nonNull(data.getRuleType())).map(data -> data.getRuleType().getValue()).sorted().distinct().toList());
        filterVO.setReviewRuleFilters(filteredData.stream().map(ReviewDetailRuleFilterDataVO::getReviewRule).distinct().toList());
        filterVO.setDeviceTypeFilters(filteredData.stream().map(data -> StringUtils.isBlank(data.getDeviceType()) ? " " : data.getDeviceType()).distinct().toList());
        filterVO.setIsPassedFilters(filteredData.stream().map(ReviewDetailRuleFilterDataVO::getIsPassed).distinct().toList());
        return filterVO;
    }

    @Override
    public List<CircuitReviewResult> getResultByFileVersionIdList(List<Long> fileVersionIdList) {
        if (CollectionUtils.isNotEmpty(fileVersionIdList)) {
            return this.lambdaQuery().in(CircuitReviewResult::getFileVersionId, fileVersionIdList).eq(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FINISHED).orderByDesc(CircuitReviewResult::getReviewTime).list();
        } else {
            return List.of();
        }
    }

    @Override
    public List<CircuitReviewResult> getResultByFileIdList(List<Long> fileIdList) {
        if (CollectionUtils.isNotEmpty(fileIdList)) {
            return this.lambdaQuery().in(CircuitReviewResult::getFileId, fileIdList).eq(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FINISHED).orderByDesc(CircuitReviewResult::getReviewTime).list();
        } else {
            return List.of();
        }
    }

    @Override
    public List<CircuitReviewResultVO> getCircuitFileVersionResults(Long versionId) {
        List<CircuitReviewResult> reviewResults = this.lambdaQuery().eq(CircuitReviewResult::getFileVersionId, versionId).eq(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FINISHED).orderByDesc(CircuitReviewResult::getReviewTime).list();
        return reviewResults.stream().map(this::convertToVO).toList();
    }

    @Override
    public void removeCircuitFileReviewResult(List<Long> removeTargets) {
        if (CollectionUtils.isNotEmpty(removeTargets)) {
            LambdaQueryWrapper<CircuitReviewResult> queryWrapper = Wrappers.<CircuitReviewResult>lambdaQuery().in(CircuitReviewResult::getFileId, removeTargets);
            List<CircuitReviewResult> reviewResults = this.list(queryWrapper);
            List<Long> reviewResultIdList = reviewResults.stream().map(CircuitReviewResult::getId).toList();
            circuitReviewResultDetailService.removeDetailByResultIdList(reviewResultIdList);
            this.removeByIds(reviewResultIdList);
        }
    }

        @Override
    public boolean deleteCircuitReviewResult(Long id) {
        log.info("开始删除审查结果 ID: {}", id);
        
        CircuitReviewResultVO circuitReviewResultVOById = getCircuitReviewResultVOById(id);
        
        // 1. 删除审查结果记录（该记录不需要再更新）
        this.removeById(id);
        log.info("已删除审查结果 ID: {}", id);
        
        // 2. 删除该审查结果对应的 Issue（这些 Issue 不再是问题）
        this.circuitReviewIssueService.remove(Wrappers.<CircuitReviewIssue>lambdaUpdate()
                .eq(CircuitReviewIssue::getResultId, circuitReviewResultVOById.getId()));
        log.info("已删除审查结果 ID: {} 对应的 Issue", id);
        
        // 3. 查询该文件剩余的所有审查结果
        List<CircuitReviewResult> circuitReviewResults = this.list(Wrappers.<CircuitReviewResult>lambdaQuery()
                .eq(CircuitReviewResult::getFileId, circuitReviewResultVOById.getFileId())
                .eq(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FINISHED));
        
        // 4. 获取删除后的文件总问题数
        Long fileReviewIssueCount = this.circuitReviewIssueService.getFileReviewIssueCount(circuitReviewResultVOById.getFileId());
        log.info("文件 ID: {} 删除后的总问题数: {}", circuitReviewResultVOById.getFileId(), fileReviewIssueCount);
        
        int maxCheckPoints = 0;
        
        // 5. 更新所有剩余审查结果的关闭问题数和最大检查点数
        for (CircuitReviewResult circuitReviewResult : circuitReviewResults) {
            // 计算最大检查点数
            if (circuitReviewResult.getCheckPoints() != null && circuitReviewResult.getCheckPoints() > maxCheckPoints) {
                maxCheckPoints = circuitReviewResult.getCheckPoints();
            }
            
            // 关闭问题数 = 文件当前总问题数 - 该审查结果还存在的问题数
            int currentCheckPoints = circuitReviewResult.getCheckPoints() != null ? circuitReviewResult.getCheckPoints() : 0;
            int currentPassCheckPoints = circuitReviewResult.getPassCheckPoints() != null ? circuitReviewResult.getPassCheckPoints() : 0;
            int currentFailedCount = currentCheckPoints - currentPassCheckPoints;
            
            int closedFailCheckPoints = fileReviewIssueCount.intValue() - currentFailedCount;
            if (closedFailCheckPoints < 0) {
                closedFailCheckPoints = 0;
            }
            
            circuitReviewResult.setClosedFailCheckPoints(closedFailCheckPoints);
            circuitReviewResult.setTotalFailCheckPoints(fileReviewIssueCount.intValue());
            this.updateById(circuitReviewResult);
            log.debug("更新审查结果 ID: {} 的关闭问题数为: {}", circuitReviewResult.getId(), closedFailCheckPoints);
        }

        // 6. 更新文件的 maxCheckPoints
        List<CircuitFile> files = circuitFileService.lambdaQuery()
                .eq(CircuitFile::getId, circuitReviewResultVOById.getFileId())
                .list();
        
        if (CollectionUtils.isNotEmpty(files)) {
            CircuitFile circuitFile = files.get(0);
            circuitFile.setMaxCheckPoints(maxCheckPoints);
            circuitFileService.updateById(circuitFile);
            log.info("更新文件 ID: {} 的 maxCheckPoints 为: {}", circuitReviewResultVOById.getFileId(), maxCheckPoints);
        }

        log.info("成功删除审查结果 ID: {}, 文件 ID: {}", id, circuitReviewResultVOById.getFileId());
        return true;
    }

    @Async
    @Transactional
    public void asyncReview(CircuitReviewResult circuitReviewResult, String filePath, Long userId, CircuitFileVersionVO circuitFileVersionVO, String userName, String departmentName) {
        try {
            // 1. 调用第三方接口
            Map<String, String> paramsMap = Map.of(
                    "filePath", filePath,
                    "userId", userName,
                    "departmentId", departmentName
            );
            ResponseWrapper<Map<String, Map<String, Object>>> responseWrapper = reviewClient.review(paramsMap);
            if (!responseWrapper.isSucc()) {
                updateReviewStatusFailed(circuitReviewResult.getId(), responseWrapper.getMsg(), userId);
                log.error("电路审查失败: {}", responseWrapper.getMsg());
                // 审查失败后删除python服务的文件
                deleteReviewFileFromThirdParty(filePath);
                return;
            }

            // 2. 处理规则
            // 3. 组装详情
            Map<String, Object> result = responseWrapper.getContent().get("data");
            @SuppressWarnings("unchecked")
            List<String> ruleTypes = (List<String>) result.get("规则类型");
            @SuppressWarnings("unchecked")
            List<String> ruleNames = (List<String>) result.get("审查规则");
            @SuppressWarnings("unchecked")
            List<String> deviceTypes = (List<String>) result.get("器件型号");
            @SuppressWarnings("unchecked")
            List<String> tagPins = (List<String>) result.get("位号引脚");
            @SuppressWarnings("unchecked")
            List<String> suggestions = (List<String>) result.get("审查意见");
            @SuppressWarnings("unchecked")
            List<String> isPassedList = (List<String>) result.get("是否通过");
            List<CircuitReviewResultDetail> details = new ArrayList<>();
            boolean isClosedLoop = true;
            int passedCount = 0;
            int failedCount = 0;
            List<CircuitReviewIssue> issues = new ArrayList<>();
            for (int i = 0; i < ruleNames.size(); i++) {
                String code = ruleNames.get(i);
                // 查找是否已存在
                List<CircuitReviewRule> circuitReviewRules = circuitReviewRuleService.list(Wrappers.<CircuitReviewRule>lambdaQuery().eq(CircuitReviewRule::getCode, code));
                if (CollectionUtils.isEmpty(circuitReviewRules)) {
                    continue;
                }
                CircuitReviewRule rule = circuitReviewRules.get(0);
                Long ruleId = rule.getId();
                RuleTypeEnum ruleType = rule.getType();

                CircuitReviewResultDetail detail = new CircuitReviewResultDetail();
                detail.setId(IdWorker.getId());
                detail.setResultId(circuitReviewResult.getId());
                detail.setRuleId(ruleId);
                detail.setRuleCode(rule.getCode());
                detail.setDeviceType(deviceTypes.get(i));
                detail.setTagPin(tagPins.get(i));
                detail.setReviewSuggestion(suggestions.get(i));
                detail.setIsPassed(isPassedList.get(i).contains("不通过") ? 0 : 1);
                detail.setCreateUser(userId);
                detail.setCreateDate(LocalDateTime.now());
                details.add(detail);

                if (StringUtils.isNotEmpty(detail.getTagPin())) {
                    // 如果存在引脚标签，则根据逗号分隔的引脚数量来计算检查点数量
                    String[] tagPinList = detail.getTagPin().split(",");
                    int checkpointCount = tagPinList.length;
                    if (detail.getIsPassed() == 0) {
                        // 如果未通过，则增加失败的检查点数量
                        failedCount = failedCount + checkpointCount;

                        // 使用每一个位号管脚创建一条电路审查问题
                        for (String tagPin : tagPinList) {
                            issues.add(new CircuitReviewIssue()
                                    .setFileId(circuitFileVersionVO.getFileId())
                                    .setFileVersionId(circuitFileVersionVO.getId())
                                    .setResultId(circuitReviewResult.getId())
                                    .setResultDetailId(detail.getId())
                                    .setRuleId(detail.getRuleId())
                                    .setRuleCode(detail.getRuleCode())
                                    .setDeviceType(detail.getDeviceType())
                                    .setTagPin(tagPin)
                                    .setReviewSuggestion(detail.getReviewSuggestion())
                                    .setReviewTime(circuitReviewResult.getReviewTime()));
                        }
                    } else {
                        // 如果通过，则增加通过的检查点数量
                        passedCount = passedCount + checkpointCount;
                    }
                } else {
                    // 如果不存在引脚标签，则每个规则计为一个检查点
                    if (detail.getIsPassed() == 0) {
                        // 如果未通过，则增加失败的检查点数量
                        failedCount++;

                        //不存在位号管脚则只创建一条电路审查问题
                        issues.add(new CircuitReviewIssue()
                                .setFileId(circuitFileVersionVO.getFileId())
                                .setFileVersionId(circuitFileVersionVO.getId())
                                .setResultId(circuitReviewResult.getId())
                                .setResultDetailId(detail.getId())
                                .setRuleId(detail.getRuleId())
                                .setRuleCode(detail.getRuleCode())
                                .setDeviceType(detail.getDeviceType())
                                .setReviewSuggestion(detail.getReviewSuggestion())
                                .setReviewTime(circuitReviewResult.getReviewTime()));
                    } else {
                        // 如果通过，则增加通过的检查点数量
                        passedCount++;
                    }
                }

                if ((ruleType.equals(RuleTypeEnum.COERCIVE_RULE) || ruleType.equals(RuleTypeEnum.NEEDFUL_RULE)) && detail.getIsPassed() == 0) {
                    isClosedLoop = false;
                }
            }

            // 4. 批量保存
            circuitReviewResultDetailService.saveBatch(details);

            // 5. 批量保存电路审查问题（自动去重）
            circuitReviewIssueService.saveIssuesBatch(issues);

            // 6. 查询该文件所有审查问题数量
            Long fileReviewIssueCount = circuitReviewIssueService.getFileReviewIssueCount(circuitFileVersionVO.getFileId());

            // 7. 继承历史复核的审查结果详情（包括APPROVED和REJECTED）
            int inheritedPassCount = inheritApprovedAuditDetails(circuitReviewResult, details, userId);

            // 8. 重新检查闭环状态（考虑继承后的approvedAuditType）
            isClosedLoop = checkResultClosedLoopAfterInherit(circuitReviewResult.getId()) == 1;

            // 9. 更新CircuitReviewResult状态（考虑继承的检查点）
            int total = passedCount + failedCount;
            int actualPassedCount = passedCount + inheritedPassCount;
            int closedFailCheckPoints = fileReviewIssueCount.intValue() - (total - actualPassedCount);
            if (closedFailCheckPoints < 0) {
                closedFailCheckPoints = 0;
            }
            updateReviewResultSuccess(circuitReviewResult.getId(), actualPassedCount, total, closedFailCheckPoints, fileReviewIssueCount.intValue(), isClosedLoop, userId);

            // 10. 判断是否需要更新文件的最大审查点数
            List<CircuitFile> files = circuitFileService.lambdaQuery()
                    .eq(CircuitFile::getId, circuitReviewResult.getFileId())
                    .list();

            if (CollectionUtils.isNotEmpty(files)) {
                CircuitFile circuitFile = files.get(0);
                Integer maxCheckPoints = circuitFile.getMaxCheckPoints();
                // 如果 maxCheckPoints 为空或当前检查点数更大，则更新
                if (maxCheckPoints == null || total > maxCheckPoints) {
                    circuitFile.setMaxCheckPoints(total);
                    circuitFileService.updateById(circuitFile);
                    log.info("更新文件 ID: {} 的 maxCheckPoints 为: {}", circuitReviewResult.getFileId(), total);
                }
            }

            // 11. 审查成功后删除python服务的文件
            deleteReviewFileFromThirdParty(filePath);
        } catch (Exception e) {
            updateReviewStatusFailed(circuitReviewResult.getId(), e.getMessage(), userId);
            log.error("电路审查异步处理失败", e);
            // 审查失败后删除python服务的文件
            deleteReviewFileFromThirdParty(filePath);
            throw new KnowException(e.getMessage(), e);
        }
    }

    private void deleteReviewFileFromThirdParty(String filePath) {
        ResponseWrapper<Void> voidResponseWrapper = reviewClient.deleteFile(filePath);
        if (!voidResponseWrapper.isSucc()) {
            log.error("删除文件失败: {}", voidResponseWrapper.getMsg());
        } else {
            log.info("删除文件成功: {}", filePath);
        }
    }

    /**
     * 更新审查结果为成功状态
     * 使用REQUIRES_NEW事务传播行为，确保在独立事务中执行
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReviewResultSuccess(Long resultId, int passedCount, int total, int closedFailCheckPoints, int fileReviewIssueCount, boolean isClosedLoop, Long userId) {
        CircuitReviewResult update = new CircuitReviewResult();
        update.setId(resultId);
        update.setPassCheckPoints(passedCount);
        update.setCheckPoints(total);
        update.setClosedFailCheckPoints(closedFailCheckPoints);
        update.setTotalFailCheckPoints(fileReviewIssueCount);
        update.setPassRate(new BigDecimal(passedCount).divide(new BigDecimal(total), 5, RoundingMode.HALF_UP));
        update.setIsClosedLoop(isClosedLoop ? 1 : 0);
        update.setStatus(CircuitReviewStatusEnum.FINISHED);
        update.setUpdateUser(userId);
        update.setUpdateDate(LocalDateTime.now());
        this.updateById(update);
    }

    /**
     * 更新审查结果为失败状态
     * 使用REQUIRES_NEW事务传播行为，确保在独立事务中执行
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReviewStatusFailed(Long resultId, String errorMessage, Long userId) {
        CircuitReviewResult update = new CircuitReviewResult();
        update.setId(resultId);
        update.setStatus(CircuitReviewStatusEnum.FAILED);
        update.setErrorMessage(errorMessage);
        update.setUpdateUser(userId);
        update.setUpdateDate(LocalDateTime.now());
        this.updateById(update);
    }

    /**
     * 判断当前位号管脚是否可以继承历史位号管脚
     * @param currentTagPin 当前位号管脚（逗号分隔）
     * @param historyTagPin 历史位号管脚（逗号分隔）
     * @return 是否可以继承
     */
    private boolean canInheritTagPin(String currentTagPin, String historyTagPin) {
        // 两者都为空，可以继承
        if (StringUtils.isEmpty(currentTagPin) && StringUtils.isEmpty(historyTagPin)) {
            return true;
        }
        // 一个为空一个不为空，不能继承
        if (StringUtils.isEmpty(currentTagPin) || StringUtils.isEmpty(historyTagPin)) {
            return false;
        }
        // 分割成集合
        Set<String> currentSet = new HashSet<>(Arrays.asList(currentTagPin.split(",")));
        Set<String> historySet = new HashSet<>(Arrays.asList(historyTagPin.split(",")));
        // 判断是否为子集
        return historySet.containsAll(currentSet);
    }

    /**
     * 检查继承后的闭环状态
     * 只要还有审查规则是强制规则和必要规则同时审查结果详情不通过且未设置approvedAuditType，就是未闭环
     */
    private int checkResultClosedLoopAfterInherit(Long resultId) {
        List<CircuitReviewRule> reviewRuleList = circuitReviewRuleService.list(
            Wrappers.<CircuitReviewRule>lambdaQuery()
                .in(CircuitReviewRule::getType, List.of(RuleTypeEnum.COERCIVE_RULE.getValue(), RuleTypeEnum.NEEDFUL_RULE.getValue()))
        );
        List<Long> ruleIdList = reviewRuleList.stream().map(CircuitReviewRule::getId).toList();
        long count = circuitReviewResultDetailService.count(
            Wrappers.<CircuitReviewResultDetail>lambdaQuery()
                .eq(CircuitReviewResultDetail::getResultId, resultId)
                .in(CircuitReviewResultDetail::getRuleId, ruleIdList)
                .eq(CircuitReviewResultDetail::getIsPassed, 0)
                .isNull(CircuitReviewResultDetail::getApprovedAuditType)
        );
        return count == 0 ? 1 : 0;
    }

    /**
     * 继承历史复核的审查结果详情
     * 查询该文件所有APPROVED和REJECTED状态的复核记录，匹配当前未通过的详情，创建新的复核记录
     * @param circuitReviewResult 当前审查结果
     * @param currentDetails 当前新生成的详情列表
     * @param userId 用户ID
     * @return 继承的通过检查点数（只统计APPROVED状态的）
     */
    private int inheritApprovedAuditDetails(CircuitReviewResult circuitReviewResult,
                                            List<CircuitReviewResultDetail> currentDetails,
                                            Long userId) {
        log.info("开始继承历史复核的审查结果详情，文件ID: {}, 审查结果ID: {}",
                circuitReviewResult.getFileId(), circuitReviewResult.getId());

        // 0. 查询该文件最近一次的复核记录（只继承最近一次的复核记录）
        List<CircuitReviewResultAudit> historyAudits = circuitReviewResultAuditService.list(
                Wrappers.<CircuitReviewResultAudit>lambdaQuery()
                        .eq(CircuitReviewResultAudit::getFileId, circuitReviewResult.getFileId())
                        .orderByDesc(CircuitReviewResultAudit::getAuditTime)
        );

        if(CollectionUtils.isEmpty(historyAudits)) {
            log.info("未找到历史复核记录，无需继承");
            return 0;
        }

        // 1. 查询该文件所有APPROVED和REJECTED状态的复核详情记录
        List<CircuitReviewResultDetailAudit> historyResultDetailAudits = circuitReviewResultDetailAuditService.list(
                Wrappers.<CircuitReviewResultDetailAudit>lambdaQuery()
                        .eq(CircuitReviewResultDetailAudit::getResultAuditId, historyAudits.get(0).getId())
                        .in(CircuitReviewResultDetailAudit::getStatus,
                                CircuitReviewResultDetailAuditStatusEnum.APPROVED,
                                CircuitReviewResultDetailAuditStatusEnum.REJECTED)
                        .orderByAsc(CircuitReviewResultDetailAudit::getAuditCloseTime)
        );

        if (CollectionUtils.isEmpty(historyResultDetailAudits)) {
            log.info("未找到历史复核记录，无需继承");
            return 0;
        }

        // 2. 提取resultDetailId列表，查询对应的详情记录
        List<Long> historyDetailIds = historyResultDetailAudits.stream()
                .map(CircuitReviewResultDetailAudit::getResultDetailId)
                .distinct()
                .toList();

        List<CircuitReviewResultDetail> historyDetails = circuitReviewResultDetailService.list(
                Wrappers.<CircuitReviewResultDetail>lambdaQuery()
                        .in(CircuitReviewResultDetail::getId, historyDetailIds)
        );

        if (CollectionUtils.isEmpty(historyDetails)) {
            log.info("未找到历史详情记录，无需继承");
            return 0;
        }

        // 3. 构建历史详情的Map，key为ruleCode+deviceType+reviewSuggestion
        Map<String, List<CircuitReviewResultDetail>> historyDetailMap = historyDetails.stream()
                .collect(Collectors.groupingBy(detail -> {
                    String key = detail.getRuleCode() + "|" +
                            (detail.getDeviceType() != null ? detail.getDeviceType() : "") + "|" +
                            (detail.getReviewSuggestion() != null ? detail.getReviewSuggestion() : "");
                    return key;
                }));

        // 4. 构建历史复核记录的Map，key为resultDetailId
        Map<Long, CircuitReviewResultDetailAudit> historyAuditMap = historyResultDetailAudits.stream()
                .collect(Collectors.toMap(
                        CircuitReviewResultDetailAudit::getResultDetailId,
                        audit -> audit,
                        (existing, replacement) -> replacement // 如果有重复，保留第一个
                ));

        // 5. 遍历当前未通过的详情，查找可继承的记录
        List<CircuitReviewResultDetail> detailsToInherit = new ArrayList<>();
        Map<Long, CircuitReviewResultDetailAudit> auditsToCreate = new HashMap<>();
        int inheritedPassCount = 0;

        for (CircuitReviewResultDetail currentDetail : currentDetails) {
            // 只处理未通过的详情
            if (currentDetail.getIsPassed() != 0) {
                continue;
            }

            // 构建匹配key
            String matchKey = currentDetail.getRuleCode() + "|" +
                             (currentDetail.getDeviceType() != null ? currentDetail.getDeviceType() : "") + "|" +
                             (currentDetail.getReviewSuggestion() != null ? currentDetail.getReviewSuggestion() : "");

            List<CircuitReviewResultDetail> matchedHistoryDetails = historyDetailMap.get(matchKey);
            if (CollectionUtils.isEmpty(matchedHistoryDetails)) {
                continue;
            }

            // 遍历匹配的歷史详情，检查位号管脚是否满足继承条件
            for (CircuitReviewResultDetail historyDetail : matchedHistoryDetails) {
                // 检查位号管脚是否可以继承
                if (canInheritTagPin(currentDetail.getTagPin(), historyDetail.getTagPin())) {
                    // 找到可继承的记录
                    detailsToInherit.add(currentDetail);

                    // 获取对应的历史复核记录
                    CircuitReviewResultDetailAudit historyAudit = historyAuditMap.get(historyDetail.getId());
                    if (historyAudit != null) {
                        auditsToCreate.put(currentDetail.getId(), historyAudit);

                        // 如果是APPROVED状态，计算继承的检查点数，同时需要排除复核类型为问题可例外的
                        if (historyAudit.getStatus() == CircuitReviewResultDetailAuditStatusEnum.APPROVED && !historyAudit.getAuditType().equals(CircuitReviewResultDetailAuditTypeEnum.ISSUE_EXCEPTION)) {
                            if (StringUtils.isNotEmpty(currentDetail.getTagPin())) {
                                inheritedPassCount += currentDetail.getTagPin().split(",").length;
                            } else {
                                inheritedPassCount += 1;
                            }
                        }
                    }
                    break; // 找到一个匹配就跳出，避免重复继承
                }
            }
        }

        if (CollectionUtils.isEmpty(detailsToInherit)) {
            log.info("未找到可继承的详情记录");
            return 0;
        }

        log.info("找到 {} 个可继承的详情记录，其中APPROVED状态的检查点数为: {}",
                detailsToInherit.size(), inheritedPassCount);

        // 6. 创建CircuitReviewResultAudit记录
        CircuitReviewResultAudit resultAudit = new CircuitReviewResultAudit();
        resultAudit.setId(IdWorker.getId());
        resultAudit.setFileId(circuitReviewResult.getFileId());
        resultAudit.setFileVersionId(circuitReviewResult.getFileVersionId());
        resultAudit.setResultId(circuitReviewResult.getId());
        resultAudit.setIsAuditFinished(1);
        resultAudit.setIsAdminAuditFinished(1);
        resultAudit.setIsExpertAuditFinished(1);
        resultAudit.setAuditTime(circuitReviewResult.getReviewTime());
        resultAudit.setCreateUser(circuitReviewResult.getCreateUser());
        resultAudit.setUpdateUser(circuitReviewResult.getUpdateUser());
        resultAudit.setCreateDate(circuitReviewResult.getCreateDate());
        resultAudit.setUpdateDate(circuitReviewResult.getUpdateDate());

        circuitReviewResultAuditService.save(resultAudit);

        // 7. 为每个可继承的详情创建CircuitReviewResultDetailAudit记录
        List<CircuitReviewResultDetailAudit> detailAuditsToSave = new ArrayList<>();

        for (CircuitReviewResultDetail detail : detailsToInherit) {
            CircuitReviewResultDetailAudit historyAudit = auditsToCreate.get(detail.getId());
            if (historyAudit == null) {
                continue;
            }

            CircuitReviewResultDetailAudit detailAudit = new CircuitReviewResultDetailAudit();
            detailAudit.setId(IdWorker.getId());
            detailAudit.setFileId(circuitReviewResult.getFileId());
            detailAudit.setFileVersionId(circuitReviewResult.getFileVersionId());
            detailAudit.setResultId(circuitReviewResult.getId());
            detailAudit.setResultDetailId(detail.getId());
            detailAudit.setResultAuditId(resultAudit.getId());
            detailAudit.setInheritedDetailAuditId(historyAudit.getId());
            detailAudit.setAuditType(historyAudit.getAuditType());
            detailAudit.setStatus(historyAudit.getStatus());
            detailAudit.setIssueFeedback(historyAudit.getIssueFeedback());
            detailAudit.setRejectReason(historyAudit.getRejectReason());
            detailAudit.setAuditSubmitTime(circuitReviewResult.getReviewTime());
            detailAudit.setAuditCloseTime(circuitReviewResult.getReviewTime());
            detailAudit.setCreateUser(historyAudit.getCreateUser());
            detailAudit.setUpdateUser(historyAudit.getUpdateUser());
            detailAudit.setCreateDate(historyAudit.getCreateDate());
            detailAudit.setUpdateDate(historyAudit.getUpdateDate());
            detailAuditsToSave.add(detailAudit);

            // 更新当前详情的auditType和approvedAuditType
            if(historyAudit.getStatus().equals(CircuitReviewResultDetailAuditStatusEnum.APPROVED)) {
                detail.setAuditType(historyAudit.getAuditType());
                detail.setApprovedAuditType(historyAudit.getAuditType());
                detail.setIssueFeedback(historyAudit.getIssueFeedback());
            }
            else if(historyAudit.getStatus().equals(CircuitReviewResultDetailAuditStatusEnum.REJECTED)) {
                detail.setAuditType(null);
                detail.setApprovedAuditType(null);
                detail.setIssueFeedback(null);
            }
        }

        // 批量保存详情复核记录
        if (CollectionUtils.isNotEmpty(detailAuditsToSave)) {
            circuitReviewResultDetailAuditService.saveBatch(detailAuditsToSave);
            log.info("创建了 {} 条详情复核记录", detailAuditsToSave.size());
        }

        // 批量更新详情的approvedAuditType
        if (CollectionUtils.isNotEmpty(detailsToInherit)) {
            circuitReviewResultDetailService.updateBatchById(detailsToInherit);
            log.info("更新了 {} 条详情的approvedAuditType", detailsToInherit.size());
        }

        return inheritedPassCount;
    }

    /**
     * 将实体转换为VO
     */
    private CircuitReviewResultVO convertToVO(CircuitReviewResult circuitReviewResult) {
        if (circuitReviewResult == null) {
            return null;
        }
        CircuitReviewResultVO circuitReviewResultVO = objectMapper.convertValue(circuitReviewResult, CircuitReviewResultVO.class);
        if (Objects.nonNull(circuitReviewResultVO.getCheckPoints())) {
            int failCheckPoints = circuitReviewResultVO.getCheckPoints() - circuitReviewResultVO.getPassCheckPoints();
            // 确保问题点数不会为负数
            if (failCheckPoints < 0) {
                failCheckPoints = 0;
            }
            circuitReviewResultVO.setFailCheckPoints(failCheckPoints);
        }
        CircuitReviewResultAuditService circuitReviewResultAuditService = SpringContextUtils.getInstanceByType(CircuitReviewResultAuditService.class);
        CircuitReviewResultAudit circuitReviewResultAudit = circuitReviewResultAuditService.getByResultId(circuitReviewResult.getId());
        if (Objects.nonNull(circuitReviewResultAudit)) {
            circuitReviewResultVO.setResultAuditId(circuitReviewResultAudit.getId());
        }
        return circuitReviewResultVO;
    }
}