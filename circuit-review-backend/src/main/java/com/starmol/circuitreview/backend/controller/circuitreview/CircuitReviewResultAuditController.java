package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileAuditVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultAuditVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.CircuitFileAuditSortFieldEnum;
import com.starmol.circuitreview.backend.constant.CircuitFileSortFieldEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultAudit;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 电路审查结果审核控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/circuit-review-result-audit")
@Tag(name = "电路审查结果审核管理", description = "电路审查结果审核相关接口")
public class CircuitReviewResultAuditController {

    private final CircuitReviewResultAuditService circuitReviewResultAuditService;

    @GetMapping("/admin/page")
    @Operation(summary = "分页查询管理员电路审查结果审核", description = "分页查询管理员电路审查结果审核列表")
    public ResponseWrapper<IPage<CircuitFileAuditVO>> getCircuitReviewResultAuditVOPageForAdmin(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件名模糊查询参数") @RequestParam(required = false) String fileName,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "状态（0：待审核；1：已审核）") @RequestParam(required = false) Integer status,
            @Parameter(description = "排序字段") @RequestParam(required = false) CircuitFileAuditSortFieldEnum sortField,
            @Parameter(description = "排序方向") @RequestParam(required = false) SortDirectionEnum sortDirection) {
        try {
            Page<CircuitFileAuditVO> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitFileAuditVO> result = circuitReviewResultAuditService.getCircuitReviewResultAuditVOPageForAdmin(page, fileName, depId, userId, status, sortField, sortDirection);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果审核失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/expert/page")
    @Operation(summary = "分页查询专家电路审查结果审核", description = "分页查询专家电路审查结果审核列表")
    public ResponseWrapper<IPage<CircuitFileAuditVO>> getCircuitReviewResultAuditVOPageForExpert(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件名模糊查询参数") @RequestParam(required = false) String fileName,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "状态（0：待审核；1：已审核）") @RequestParam(required = false) Integer status,
            @Parameter(description = "排序字段") @RequestParam(required = false) CircuitFileAuditSortFieldEnum sortField,
            @Parameter(description = "排序方向") @RequestParam(required = false) SortDirectionEnum sortDirection) {
        try {
            Page<CircuitFileAuditVO> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitFileAuditVO> result = circuitReviewResultAuditService.getCircuitReviewResultAuditVOPageForExpert(page, fileName, depId, userId, status, sortField, sortDirection);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果审核失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果审核失败: " + e.getMessage());
        }
    }
}