package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailAuditVO;
import com.starmol.circuitreview.backend.constant.AuditActionTypeEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetailAudit;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewResultDetailAuditMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultDetailAuditService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultDetailService;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电路审查结果详情审核服务实现类
 */
@Service
@RequiredArgsConstructor
public class CircuitReviewResultDetailAuditServiceImpl extends BaseCascadeServiceImpl<CircuitReviewResultDetailAuditMapper, CircuitReviewResultDetailAudit> implements CircuitReviewResultDetailAuditService {

    private final ObjectMapper objectMapper;

    @Override
    public IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPageForAdmin(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId) {
        return getCircuitReviewResultDetailAuditVOPage(page, resultAuditId, true);
    }

    @Override
    public IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPageForExpert(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId) {
        return getCircuitReviewResultDetailAuditVOPage(page, resultAuditId, false);
    }

    private IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPage(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId, boolean isAdmin) {
        List<CircuitReviewResultDetailAuditTypeEnum> auditTypeEnumList;
        if(isAdmin) {
            auditTypeEnumList = List.of(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT);
        }
        else {
            auditTypeEnumList = List.of(CircuitReviewResultDetailAuditTypeEnum.RULE_NOT_APPLICABLE, CircuitReviewResultDetailAuditTypeEnum.ISSUE_EXCEPTION, CircuitReviewResultDetailAuditTypeEnum.REVIEW_NO_IMPACT);
        }

        return this.getBaseMapper().getCircuitReviewResultDetailAuditVOPage(page, resultAuditId, auditTypeEnumList);
    }

    @Override
    public CircuitReviewResultDetailAuditVO getCircuitReviewResultDetailAuditVOById(Long id) {
        CircuitReviewResultDetailAudit entity = this.getById(id);
        if (entity != null) {
            return objectMapper.convertValue(entity, CircuitReviewResultDetailAuditVO.class);
        }
        else {
            throw new KnowException("电路审查结果详情审核不存在");
        }
    }

    @Override
    public void updateCircuitReviewResultDetailAudit(Long id, AuditActionTypeEnum auditActionType, String rejectReason) {
        CircuitReviewResultDetailService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultDetailService.class);
        switch (auditActionType) {
            case APPROVE -> circuitReviewResultService.auditApprove(id);
            case REJECT -> circuitReviewResultService.auditReject(id, rejectReason);
        }
    }
}