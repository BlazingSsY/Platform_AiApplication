package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailVO;
import com.starmol.circuitreview.backend.bean.vo.ReviewDetailRuleFilterDataVO;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetail;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 电路审查结果详情服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitReviewResultDetailService extends BaseService<CircuitReviewResultDetail> {


    /**
     * 创建电路审查结果详情并返回VO
     */
    CircuitReviewResultDetailVO createCircuitReviewResultDetail(CircuitReviewResultDetail circuitReviewResultDetail);


    void removeDetailByResultIdList(List<Long> resultIdList);

    /**
     * 更新电路审查结果详情并返回VO
     */
    CircuitReviewResultDetailVO updateCircuitReviewResultDetail(Long id, CircuitReviewResultDetail circuitReviewResultDetail);
    
    /**
     * 根据ID获取电路审查结果详情VO
     */
    CircuitReviewResultDetailVO getCircuitReviewResultDetailVOById(Long id);
    
    /**
     * 分页查询电路审查结果详情VO
     */
    IPage<CircuitReviewResultDetailVO> getCircuitReviewResultDetailVOPage(IPage<CircuitReviewResultDetailVO> page, Long resultId, String ruleName, RuleTypeEnum ruleType, String deviceType, String tagPin, String reviewSuggestion, Integer isPassed);

    /**
     * 查询电路审查结果详情VO列表
     */
    List<CircuitReviewResultDetailVO> getCircuitReviewResultDetailVOList(Long resultId, String ruleName, RuleTypeEnum ruleType, String deviceType, String tagPin, String reviewSuggestion, Integer isPassed);

    List<CircuitReviewResultDetailVO> getAllDetailsByResultId(Long resultId);

    List<ReviewDetailRuleFilterDataVO> getCircuitReviewResultDetailFilters(Long id);

    CircuitReviewResultDetailVO submitCircuitReviewResultDetailAudit(Long id, CircuitReviewResultDetailAuditTypeEnum auditType, String issueFeedback);

    void auditApprove(Long detailAuditId);

    void auditReject(Long detailAuditId, String rejectReason);

    int checkResultClosedLoopPublic(Long resultId);
}