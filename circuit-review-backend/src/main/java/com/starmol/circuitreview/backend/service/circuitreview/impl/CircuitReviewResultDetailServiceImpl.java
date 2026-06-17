package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailVO;
import com.starmol.circuitreview.backend.bean.vo.ReviewDetailRuleFilterDataVO;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditStatusEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.circuitreview.*;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewResultDetailMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.*;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import com.starmol.circuitreview.backend.utils.IdWorker;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 电路审查结果详情服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitReviewResultDetailServiceImpl extends BaseServiceImpl<CircuitReviewResultDetailMapper, CircuitReviewResultDetail> implements CircuitReviewResultDetailService {

    private final ObjectMapper objectMapper;
    private final CircuitFileService circuitFileService;
    private final CircuitFileVersionService circuitFileVersionService;
    private final CircuitReviewRuleService circuitReviewRuleService;
    private final CircuitReviewResultAuditService circuitReviewResultAuditService;
    private final CircuitReviewResultDetailAuditService circuitReviewResultDetailAuditService;

    @Override
    public CircuitReviewResultDetailVO createCircuitReviewResultDetail(CircuitReviewResultDetail circuitReviewResultDetail) {
        CircuitReviewResultDetail savedCircuitReviewResultDetail = this.saveAndReturnObject(circuitReviewResultDetail);
        return convertToVO(savedCircuitReviewResultDetail);
    }

    @Override
    public void removeDetailByResultIdList(List<Long> resultIdList){
        if (CollectionUtils.isNotEmpty(resultIdList)) {
            this.remove(Wrappers.<CircuitReviewResultDetail>lambdaQuery().in(CircuitReviewResultDetail::getResultId, resultIdList));
        }
    }


    @Override
    public CircuitReviewResultDetailVO updateCircuitReviewResultDetail(Long id, CircuitReviewResultDetail circuitReviewResultDetail) {
        circuitReviewResultDetail.setId(id);
        CircuitReviewResultDetail updatedCircuitReviewResultDetail = this.updateByIDAndReturnObject(circuitReviewResultDetail);
        return convertToVO(updatedCircuitReviewResultDetail);
    }

    @Override
    public CircuitReviewResultDetailVO getCircuitReviewResultDetailVOById(Long id) {
        CircuitReviewResultDetail circuitReviewResultDetail = this.getById(id);
        return convertToVO(circuitReviewResultDetail);
    }

    @Override
    public IPage<CircuitReviewResultDetailVO> getCircuitReviewResultDetailVOPage(IPage<CircuitReviewResultDetailVO> page, Long resultId, String ruleName, RuleTypeEnum ruleType, String deviceType, String tagPin, String reviewSuggestion, Integer isPassed) {
        Integer emptyDeviceType  = Objects.nonNull(deviceType) && deviceType.trim() == "" ? 1 : 0;
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        // ruleType为枚举，传递数据库中的int值
        return this.getBaseMapper().selectDetailPageWithRule(
                page,
                resultId,
                ruleName,
                ruleType != null ? ruleType.getValue() : null,
                deviceType,
                tagPin,
                reviewSuggestion,
                isPassed,
                emptyDeviceType
        );
    }

    @Override
    public List<CircuitReviewResultDetailVO> getCircuitReviewResultDetailVOList(Long resultId, String ruleName, RuleTypeEnum ruleType, String deviceType, String tagPin, String reviewSuggestion, Integer isPassed) {
        Integer emptyDeviceType  = Objects.nonNull(deviceType) && deviceType.trim() == "" ? 1 : 0;
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        // ruleType为枚举，传递数据库中的int值
        IPage<CircuitReviewResultDetailVO> circuitReviewResultDetailVOIPage = this.getBaseMapper().selectDetailPageWithRule(
                new Page<CircuitReviewResultDetailVO>(1, 10000),
                resultId,
                ruleName,
                ruleType != null ? ruleType.getValue() : null,
                deviceType,
                tagPin,
                reviewSuggestion,
                isPassed,
                emptyDeviceType
        );
        return circuitReviewResultDetailVOIPage.getRecords();
    }

    @Override
    public List<CircuitReviewResultDetailVO> getAllDetailsByResultId(Long resultId) {
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        return this.getBaseMapper().getAllDetailsByResultId(resultId);
    }

    @Override
    public List<ReviewDetailRuleFilterDataVO> getCircuitReviewResultDetailFilters(Long id) {
        return this.getBaseMapper().getCircuitReviewResultDetailFilters(id);
    }

    @Override
    public CircuitReviewResultDetailVO submitCircuitReviewResultDetailAudit(Long id, CircuitReviewResultDetailAuditTypeEnum auditType, String issueFeedback) {
        CircuitReviewResultDetail circuitReviewResultDetail = this.getById(id);
        if(Objects.nonNull(circuitReviewResultDetail)) {
            CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
            CircuitReviewResult circuitReviewResult = circuitReviewResultService.getById(circuitReviewResultDetail.getResultId());
            List<CircuitReviewResultAudit> circuitReviewResultAuditList = circuitReviewResultAuditService.list(Wrappers.<CircuitReviewResultAudit>lambdaQuery().eq(CircuitReviewResultAudit::getFileId, circuitReviewResult.getFileId()).eq(CircuitReviewResultAudit::getIsAuditFinished, 0));
            if(CollectionUtils.isNotEmpty(circuitReviewResultAuditList) && !circuitReviewResultAuditList.stream().filter(circuitReviewResultAudit -> !circuitReviewResultAudit.getResultId().equals(circuitReviewResult.getId())).toList().isEmpty()) {
                throw new KnowException("该电路图有正在审核的审查结果");
            }
            if(circuitReviewResultDetail.getIsPassed() == 0) {
                auditSubmit(circuitReviewResult, circuitReviewResultDetail, auditType, issueFeedback);
//                switch (auditType) {
//                    case RULE_NOT_APPLICABLE:
//                    case REVIEW_NO_IMPACT:
//                        auditSubmitNoApproveNeeded(circuitReviewResult, circuitReviewResultDetail, auditType,true);
//                        break;
//                    case ISSUE_EXCEPTION:
//                        auditSubmitNoApproveNeeded(circuitReviewResult, circuitReviewResultDetail, auditType,false);
//                        break;
//                    case REVIEW_INCORRECT:
//                        auditSubmit(circuitReviewResult, circuitReviewResultDetail, auditType, issueFeedback);
//                }
            }
            else {
                throw new KnowException("电路审查结果详情已通过");
            }
        }
        else {
            throw new KnowException("电路审查结果详情不存在");
        }
        return null;
    }

    private void auditSubmitNoApproveNeeded(CircuitReviewResult circuitReviewResult, CircuitReviewResultDetail circuitReviewResultDetail, CircuitReviewResultDetailAuditTypeEnum auditType, boolean changePassCheckPointCount) {
        if (Objects.isNull(circuitReviewResultDetail.getAuditType())) {
            circuitReviewResultDetail.setAuditType(auditType);
            circuitReviewResultDetail.setApprovedAuditType(auditType);
            CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
            if (changePassCheckPointCount) {
                if(StringUtils.isNotEmpty(circuitReviewResultDetail.getTagPin())) {
                    int checkpointCount = circuitReviewResultDetail.getTagPin().split(",").length;
                    circuitReviewResult.setPassCheckPoints(circuitReviewResult.getPassCheckPoints() + checkpointCount);
                    circuitReviewResult.setPassRate(new BigDecimal(circuitReviewResult.getPassCheckPoints()).divide(new BigDecimal(circuitReviewResult.getCheckPoints()), 5, RoundingMode.HALF_UP));
                }
            }
            int isClosedLoop = checkResultClosedLoop(circuitReviewResult.getId());
            circuitReviewResult.setIsClosedLoop(isClosedLoop);
            this.updateById(circuitReviewResultDetail);
            circuitReviewResultService.updateById(circuitReviewResult);
            if(isClosedLoop == 1) {
                CircuitFile circuitFile = circuitFileService.getById(circuitReviewResult.getFileId());
                CircuitFileVersion circuitFileVersion = circuitFileVersionService.getById(circuitReviewResult.getFileVersionId());
                if(Objects.isNull(circuitFile.getClosedLoopFileVersionId()) || (Objects.nonNull(circuitFile.getClosedLoopFileVersion()) && circuitFile.getClosedLoopFileVersion() < circuitFileVersion.getFileVersion())) {
                    circuitFile.setClosedLoopFileVersionId(circuitFileVersion.getId());
                    circuitFile.setClosedLoopFileVersion(circuitFileVersion.getFileVersion());
                    circuitFile.setClosedLoopResultId(circuitReviewResult.getId());
                    circuitFile.setIsClosedLoop(isClosedLoop);
                    circuitFileService.updateById(circuitFile);
                }
            }
        }
        else {
            throw new KnowException("电路审查结果详情已提交审核");
        }
    }

    private void auditSubmit(CircuitReviewResult circuitReviewResult, CircuitReviewResultDetail circuitReviewResultDetail, CircuitReviewResultDetailAuditTypeEnum auditType, String issueFeedback) {
        circuitReviewResultDetail.setAuditType(auditType);
        circuitReviewResultDetail.setIssueFeedback(issueFeedback);
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        circuitReviewResult.setIsInAudit(1);
        this.updateById(circuitReviewResultDetail);
        circuitReviewResultService.updateById(circuitReviewResult);

        CircuitReviewResultAudit circuitReviewResultAudit = circuitReviewResultAuditService.getByResultId(circuitReviewResultDetail.getResultId());
        LocalDateTime now = LocalDateTime.now();
        if(Objects.nonNull(circuitReviewResultAudit)) {
            if(circuitReviewResultAudit.getIsAuditFinished() == 1) {
                circuitReviewResultAudit.setIsAuditFinished(0);
                circuitReviewResultAudit.setAuditTime(now);
                if(auditType.equals(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT)) {
                    circuitReviewResultAudit.setIsAdminAuditFinished(0);
                }
                else {
                    circuitReviewResultAudit.setIsExpertAuditFinished(0);
                }
                circuitReviewResultAuditService.updateById(circuitReviewResultAudit);
            } else {
                if(auditType.equals(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT)) {
                    circuitReviewResultAudit.setIsAdminAuditFinished(0);
                }
                else {
                    circuitReviewResultAudit.setIsExpertAuditFinished(0);
                }
                circuitReviewResultAuditService.updateById(circuitReviewResultAudit);
            }
        }
        else {
            circuitReviewResultAudit = new CircuitReviewResultAudit();
            circuitReviewResultAudit.setId(IdWorker.getId());
            circuitReviewResultAudit.setFileId(circuitReviewResult.getFileId());
            circuitReviewResultAudit.setFileVersionId(circuitReviewResult.getFileVersionId());
            circuitReviewResultAudit.setResultId(circuitReviewResult.getId());
            circuitReviewResultAudit.setIsAuditFinished(0);
            circuitReviewResultAudit.setAuditTime(now);
            if(auditType.equals(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT)) {
                circuitReviewResultAudit.setIsAdminAuditFinished(0);
            }
            else {
                circuitReviewResultAudit.setIsExpertAuditFinished(0);
            }
            circuitReviewResultAuditService.saveAndReturnObject(circuitReviewResultAudit);
        }
        CircuitReviewResultDetailAudit detailAudit = new CircuitReviewResultDetailAudit();
        detailAudit.setId(IdWorker.getId());
        detailAudit.setFileId(circuitReviewResult.getFileId());
        detailAudit.setFileVersionId(circuitReviewResult.getFileVersionId());
        detailAudit.setResultId(circuitReviewResult.getId());
        detailAudit.setResultDetailId(circuitReviewResultDetail.getId());
        detailAudit.setResultAuditId(circuitReviewResultAudit.getId());
        detailAudit.setAuditType(auditType);
        detailAudit.setIssueFeedback(issueFeedback);
        detailAudit.setStatus(CircuitReviewResultDetailAuditStatusEnum.IN_PROCESS);
        detailAudit.setAuditSubmitTime(now);
        circuitReviewResultDetailAuditService.saveAndReturnObject(detailAudit);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void auditApprove(Long detailAuditId) {
        Long userId = HttpRequestUtil.getUserId();
        CircuitReviewResultDetailAudit detailAudit = circuitReviewResultDetailAuditService.getById(detailAuditId);
        detailAudit.setAuditCloseTime(LocalDateTime.now());
        detailAudit.setUpdateUser(userId);
        detailAudit.setStatus(CircuitReviewResultDetailAuditStatusEnum.APPROVED);
        circuitReviewResultDetailAuditService.updateById(detailAudit);
        List<CircuitReviewResultDetailAudit> inProcessResultDetailAuditList = circuitReviewResultDetailAuditService.list(Wrappers.<CircuitReviewResultDetailAudit>lambdaQuery().eq(CircuitReviewResultDetailAudit::getResultAuditId, detailAudit.getResultAuditId()).eq(CircuitReviewResultDetailAudit::getStatus, CircuitReviewResultDetailAuditStatusEnum.IN_PROCESS));
        long adminInProgressAuditCount = inProcessResultDetailAuditList.stream().filter(circuitReviewResultDetailAudit -> circuitReviewResultDetailAudit.getAuditType().equals(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT)).count();
        long expertInProgressAuditCount = inProcessResultDetailAuditList.size() - adminInProgressAuditCount;
        CircuitReviewResultDetail circuitReviewResultDetail = this.getById(detailAudit.getResultDetailId());
        circuitReviewResultDetail.setApprovedAuditType(circuitReviewResultDetail.getAuditType());
        this.updateById(circuitReviewResultDetail);
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        CircuitReviewResult circuitReviewResult = circuitReviewResultService.getById(circuitReviewResultDetail.getResultId());
        if (!detailAudit.getAuditType().equals(CircuitReviewResultDetailAuditTypeEnum.ISSUE_EXCEPTION)) {
            Integer checkpointCount;
            // 如果存在引脚标签，则根据逗号分隔的引脚数量来计算检查点数量
            if (StringUtils.isNotEmpty(circuitReviewResultDetail.getTagPin())) {
                checkpointCount = circuitReviewResultDetail.getTagPin().split(",").length;
            }
            // 如果不存在引脚标签，则每个规则计为一个检查点
            else {
                checkpointCount = 1;
            }
            int passCheckPoints = circuitReviewResult.getPassCheckPoints() + checkpointCount;
            circuitReviewResult.setPassCheckPoints(passCheckPoints);
            int failCheckPoints = circuitReviewResult.getCheckPoints() - passCheckPoints;
            int closedFailCheckPoints = circuitReviewResult.getTotalFailCheckPoints() - failCheckPoints;
            if (closedFailCheckPoints < 0) {
                closedFailCheckPoints = 0;
            }
            circuitReviewResult.setClosedFailCheckPoints(closedFailCheckPoints);
            circuitReviewResult.setPassRate(new BigDecimal(circuitReviewResult.getPassCheckPoints()).divide(new BigDecimal(circuitReviewResult.getCheckPoints()), 5, RoundingMode.HALF_UP));
        }
        circuitReviewResult.setIsInAudit(inProcessResultDetailAuditList.isEmpty() ? 0 : 1);
        int isClosedLoop = checkResultClosedLoop(circuitReviewResult.getId());
        circuitReviewResult.setIsClosedLoop(isClosedLoop);
        circuitReviewResultService.updateById(circuitReviewResult);
        CircuitReviewResultAudit reviewResultAudit = circuitReviewResultAuditService.getById(detailAudit.getResultAuditId());
        reviewResultAudit.setIsAuditFinished(inProcessResultDetailAuditList.isEmpty() ? 1 : 0);
        reviewResultAudit.setIsAdminAuditFinished(adminInProgressAuditCount == 0 ? 1 : 0);
        reviewResultAudit.setIsExpertAuditFinished(expertInProgressAuditCount == 0 ? 1 : 0);
        circuitReviewResultAuditService.updateById(reviewResultAudit);
        if(isClosedLoop == 1) {
            CircuitFile circuitFile = circuitFileService.getById(circuitReviewResult.getFileId());
            CircuitFileVersion circuitFileVersion = circuitFileVersionService.getById(circuitReviewResult.getFileVersionId());
            if(Objects.isNull(circuitFile.getClosedLoopFileVersionId()) || (Objects.nonNull(circuitFile.getClosedLoopFileVersion()) && circuitFile.getClosedLoopFileVersion() < circuitFileVersion.getFileVersion())) {
                circuitFile.setClosedLoopFileVersionId(circuitFileVersion.getId());
                circuitFile.setClosedLoopFileVersion(circuitFileVersion.getFileVersion());
                circuitFile.setClosedLoopResultId(circuitReviewResult.getId());
                circuitFile.setIsClosedLoop(isClosedLoop);
                circuitFileService.updateById(circuitFile);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void auditReject(Long detailAuditId, String rejectReason) {
        Long userId = HttpRequestUtil.getUserId();
        CircuitReviewResultDetailAudit detailAudit = circuitReviewResultDetailAuditService.getById(detailAuditId);
        detailAudit.setAuditCloseTime(LocalDateTime.now());
        detailAudit.setStatus(CircuitReviewResultDetailAuditStatusEnum.REJECTED);
        detailAudit.setUpdateUser(userId);
        detailAudit.setRejectReason(rejectReason);
        circuitReviewResultDetailAuditService.updateById(detailAudit);
        this.update(Wrappers.<CircuitReviewResultDetail>lambdaUpdate().eq(CircuitReviewResultDetail::getId, detailAudit.getResultDetailId()).set(CircuitReviewResultDetail::getAuditType, null).set(CircuitReviewResultDetail::getIssueFeedback, null));
        List<CircuitReviewResultDetailAudit> inProcessResultDetailAuditList = circuitReviewResultDetailAuditService.list(Wrappers.<CircuitReviewResultDetailAudit>lambdaQuery().eq(CircuitReviewResultDetailAudit::getResultAuditId, detailAudit.getResultAuditId()).eq(CircuitReviewResultDetailAudit::getStatus, CircuitReviewResultDetailAuditStatusEnum.IN_PROCESS));
        long adminInProgressAuditCount = inProcessResultDetailAuditList.stream().filter(circuitReviewResultDetailAudit -> circuitReviewResultDetailAudit.getAuditType().equals(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT)).count();
        long expertInProgressAuditCount = inProcessResultDetailAuditList.size() - adminInProgressAuditCount;
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        CircuitReviewResult circuitReviewResult = circuitReviewResultService.getById(detailAudit.getResultId());
        circuitReviewResult.setIsInAudit(inProcessResultDetailAuditList.isEmpty() ? 0 : 1);
        circuitReviewResultService.updateById(circuitReviewResult);
        CircuitReviewResultAudit reviewResultAudit = circuitReviewResultAuditService.getById(detailAudit.getResultAuditId());
        reviewResultAudit.setIsAuditFinished(inProcessResultDetailAuditList.isEmpty() ? 1 : 0);
        reviewResultAudit.setIsAdminAuditFinished(adminInProgressAuditCount == 0 ? 1 : 0);
        reviewResultAudit.setIsExpertAuditFinished(expertInProgressAuditCount == 0 ? 1 : 0);
        circuitReviewResultAuditService.updateById(reviewResultAudit);
    }

    private int checkResultClosedLoop(Long resultId) {
        List<CircuitReviewRule> reviewRuleList = circuitReviewRuleService.list(Wrappers.<CircuitReviewRule>lambdaQuery().in(CircuitReviewRule::getType, List.of(RuleTypeEnum.COERCIVE_RULE.getValue(), RuleTypeEnum.NEEDFUL_RULE.getValue())));
        List<Long> ruleIdList = reviewRuleList.stream().map(CircuitReviewRule::getId).toList();
        long count = this.count(Wrappers.<CircuitReviewResultDetail>lambdaQuery().eq(CircuitReviewResultDetail::getResultId, resultId).in(CircuitReviewResultDetail::getRuleId, ruleIdList).eq(CircuitReviewResultDetail::getIsPassed, 0).isNull(CircuitReviewResultDetail::getApprovedAuditType));
        return count == 0 ? 1 : 0;
    }

    @Override
    public int checkResultClosedLoopPublic(Long resultId) {
        List<CircuitReviewRule> reviewRuleList = circuitReviewRuleService.list(Wrappers.<CircuitReviewRule>lambdaQuery().in(CircuitReviewRule::getType, List.of(RuleTypeEnum.COERCIVE_RULE.getValue(), RuleTypeEnum.NEEDFUL_RULE.getValue())));
        List<Long> ruleIdList = reviewRuleList.stream().map(CircuitReviewRule::getId).toList();
        long count = this.count(Wrappers.<CircuitReviewResultDetail>lambdaQuery().eq(CircuitReviewResultDetail::getResultId, resultId).in(CircuitReviewResultDetail::getRuleId, ruleIdList).eq(CircuitReviewResultDetail::getIsPassed, 0).isNull(CircuitReviewResultDetail::getApprovedAuditType));
        return count == 0 ? 1 : 0;
    }

    /**
     * 将实体转换为VO
     */
    private CircuitReviewResultDetailVO convertToVO(CircuitReviewResultDetail circuitReviewResultDetail) {
        if (circuitReviewResultDetail == null) {
            return null;
        }
        return objectMapper.convertValue(circuitReviewResultDetail, CircuitReviewResultDetailVO.class);
    }
} 