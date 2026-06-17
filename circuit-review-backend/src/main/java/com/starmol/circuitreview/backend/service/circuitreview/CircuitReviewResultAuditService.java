package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileAuditVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultAuditVO;
import com.starmol.circuitreview.backend.constant.CircuitFileAuditSortFieldEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultAudit;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;

/**
 * 电路审查结果审核服务接口
 */
public interface CircuitReviewResultAuditService extends BaseCascadeService<CircuitReviewResultAudit> {

    /**
     * 分页查询管理员电路审查结果审核VO
     */
    IPage<CircuitFileAuditVO> getCircuitReviewResultAuditVOPageForAdmin(Page<CircuitFileAuditVO> page, String fileName, Long depId, Long userId, Integer status, CircuitFileAuditSortFieldEnum sortField, SortDirectionEnum sortDirection);

    /**
     * 分页查询审核专家电路审查结果审核VO
     */
    IPage<CircuitFileAuditVO> getCircuitReviewResultAuditVOPageForExpert(Page<CircuitFileAuditVO> page, String fileName, Long depId, Long userId, Integer status, CircuitFileAuditSortFieldEnum sortField, SortDirectionEnum sortDirection);

    CircuitReviewResultAudit getByResultId(Long resultId);
}