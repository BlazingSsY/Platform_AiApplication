package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailAuditUpdateVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailAuditVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetailAudit;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultDetailAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 电路审查结果详情审核控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/circuit-review-result-detail-audit")
@Tag(name = "电路审查结果详情审核管理", description = "电路审查结果详情审核相关接口")
public class CircuitReviewResultDetailAuditController {

    private final CircuitReviewResultDetailAuditService circuitReviewResultDetailAuditService;

    @PutMapping("/{id}")
    @Operation(summary = "更新电路审查结果详情审核", description = "根据ID更新电路审查结果详情审核")
    public ResponseWrapper<Void> updateCircuitReviewResultDetailAudit(
            @Parameter(description = "电路审查结果详情审核ID") @PathVariable Long id,
            @RequestBody CircuitReviewResultDetailAuditUpdateVO updateVO) {
        try {
            circuitReviewResultDetailAuditService.updateCircuitReviewResultDetailAudit(
                    id, updateVO.getAuditActionType(), updateVO.getRejectReason());
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("更新电路审查结果详情审核失败", e);
            return ResponseWrapper.fail("更新电路审查结果详情审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路审查结果详情审核", description = "根据ID获取电路审查结果详情审核详情")
    public ResponseWrapper<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAudit(
            @Parameter(description = "电路审查结果详情审核ID") @PathVariable Long id) {
        try {
            CircuitReviewResultDetailAuditVO result = circuitReviewResultDetailAuditService.getCircuitReviewResultDetailAuditVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("电路审查结果详情审核不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查结果详情审核失败", e);
            return ResponseWrapper.fail("获取电路审查结果详情审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/admin/page")
    @Operation(summary = "分页查询管理员电路审查结果详情审核", description = "分页查询管理员电路审查结果详情审核列表")
    public ResponseWrapper<IPage<CircuitReviewResultDetailAuditVO>> getCircuitReviewResultDetailAuditPageForAdmin(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "电路审查结果审核id") @RequestParam Long resultAuditId) {
        try {
            Page<CircuitReviewResultDetailAudit> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitReviewResultDetailAuditVO> result = circuitReviewResultDetailAuditService.getCircuitReviewResultDetailAuditVOPageForAdmin(page, resultAuditId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果详情审核失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果详情审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/expert/page")
    @Operation(summary = "分页查询专家电路审查结果详情审核", description = "分页查询专家电路审查结果详情审核列表")
    public ResponseWrapper<IPage<CircuitReviewResultDetailAuditVO>> getCircuitReviewResultDetailAuditPageForExpert(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "电路审查结果审核id") @RequestParam Long resultAuditId) {
        try {
            Page<CircuitReviewResultDetailAudit> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitReviewResultDetailAuditVO> result = circuitReviewResultDetailAuditService.getCircuitReviewResultDetailAuditVOPageForExpert(page, resultAuditId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果详情审核失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果详情审核失败: " + e.getMessage());
        }
    }
}