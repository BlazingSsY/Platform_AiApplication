package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailAuditVO;
import com.starmol.circuitreview.backend.constant.AuditActionTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetailAudit;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;

/**
 * 电路审查结果详情审核服务接口
 */
public interface CircuitReviewResultDetailAuditService extends BaseCascadeService<CircuitReviewResultDetailAudit> {

    /**
     * 分页查询电路审查结果详情审核VO
     */
    IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPageForAdmin(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId);

    /**
     * 分页查询电路审查结果详情审核VO
     */
    IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPageForExpert(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId);

    /**
     * 根据ID获取电路审查结果详情审核VO
     */
    CircuitReviewResultDetailAuditVO getCircuitReviewResultDetailAuditVOById(Long id);

    /**
     * 更新电路审查结果详情审核
     */
    void updateCircuitReviewResultDetailAudit(Long id, AuditActionTypeEnum auditActionType, String rejectReason);
}