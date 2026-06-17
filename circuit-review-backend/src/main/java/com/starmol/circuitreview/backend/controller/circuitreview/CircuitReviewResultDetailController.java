package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.aop.Permit;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailSubmitAuditVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailVO;
import com.starmol.circuitreview.backend.common.Permission;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetail;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 电路审查结果详情控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/circuit-review-result-detail")
@Tag(name = "电路审查结果详情管理", description = "电路审查结果详情相关接口")
public class CircuitReviewResultDetailController {

    @Autowired
    private CircuitReviewResultDetailService circuitReviewResultDetailService;

    @PostMapping
    @Operation(summary = "创建电路审查结果详情", description = "创建新的电路审查结果详情")
    public ResponseWrapper<CircuitReviewResultDetailVO> createCircuitReviewResultDetail(@RequestBody CircuitReviewResultDetail circuitReviewResultDetail) {
        try {
            CircuitReviewResultDetailVO result = circuitReviewResultDetailService.createCircuitReviewResultDetail(circuitReviewResultDetail);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路审查结果详情失败", e);
            return ResponseWrapper.fail("创建电路审查结果详情失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新电路审查结果详情", description = "根据ID更新电路审查结果详情")
    public ResponseWrapper<CircuitReviewResultDetailVO> updateCircuitReviewResultDetail(
            @Parameter(description = "电路审查结果详情ID") @PathVariable Long id,
            @RequestBody CircuitReviewResultDetail circuitReviewResultDetail) {
        try {
            CircuitReviewResultDetailVO result = circuitReviewResultDetailService.updateCircuitReviewResultDetail(id, circuitReviewResultDetail);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新电路审查结果详情失败", e);
            return ResponseWrapper.fail("更新电路审查结果详情失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/audit")
    @Operation(summary = "电路审查结果详情提交审核", description = "电路审查结果详情提交审核")
    public ResponseWrapper<CircuitReviewResultDetailVO> submitCircuitReviewResultDetailAudit(
            @Parameter(description = "电路审查结果详情ID") @PathVariable Long id,
            @RequestBody CircuitReviewResultDetailSubmitAuditVO auditRequest) {
        try {
            CircuitReviewResultDetailVO result = circuitReviewResultDetailService.submitCircuitReviewResultDetailAudit(id, auditRequest.getAuditType(), auditRequest.getIssueFeedback());
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("电路审查结果详情提交审核失败", e);
            return ResponseWrapper.fail("电路审查结果详情提交审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路审查结果详情", description = "根据ID获取电路审查结果详情")
    public ResponseWrapper<CircuitReviewResultDetailVO> getCircuitReviewResultDetail(
            @Parameter(description = "电路审查结果详情ID") @PathVariable Long id) {
        try {
            CircuitReviewResultDetailVO result = circuitReviewResultDetailService.getCircuitReviewResultDetailVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("电路审查结果详情不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查结果详情失败", e);
            return ResponseWrapper.fail("获取电路审查结果详情失败: " + e.getMessage());
        }
    }

    @GetMapping("test")
    @Operation(summary = "test", description = "test")
    public ResponseWrapper<Integer> test(
            @Parameter(description = "电路审查结果详情ID") @RequestParam Long id) {
        try {
            int result = circuitReviewResultDetailService.checkResultClosedLoopPublic(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查结果详情失败", e);
            return ResponseWrapper.fail("获取电路审查结果详情失败: " + e.getMessage());
        }
    }


    @Permit(value = Permission.ALL)
    @GetMapping("/page")
    @Operation(summary = "分页查询电路审查结果详情", description = "分页查询电路审查结果详情列表")
    public ResponseWrapper<IPage<CircuitReviewResultDetailVO>> getCircuitReviewResultDetailPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId,
            @Parameter(description = "规则名称") @RequestParam(required = false) String ruleName,
            @Parameter(description = "规则类型") @RequestParam(required = false) RuleTypeEnum ruleType,
            @Parameter(description = "器件型号") @RequestParam(required = false) String deviceType,
            @Parameter(description = "位号引脚") @RequestParam(required = false) String tagPin,
            @Parameter(description = "审查意见") @RequestParam(required = false) String reviewSuggestion,
            @Parameter(description = "是否通过(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed
            ) {
        try {
            Page<CircuitReviewResultDetailVO> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitReviewResultDetailVO> result = circuitReviewResultDetailService.getCircuitReviewResultDetailVOPage(page, resultId, ruleName, ruleType, deviceType, tagPin, reviewSuggestion, isPassed);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果详情失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果详情失败: " + e.getMessage());
        }
    }


    @Permit(value = Permission.ALL)
    @GetMapping
    @Operation(summary = "查询电路审查结果详情", description = "查询电路审查结果详情列表")
    public ResponseWrapper<List<CircuitReviewResultDetailVO>> getCircuitReviewResultDetailList(
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId,
            @Parameter(description = "规则名称") @RequestParam(required = false) String ruleName,
            @Parameter(description = "规则类型") @RequestParam(required = false) RuleTypeEnum ruleType,
            @Parameter(description = "器件型号") @RequestParam(required = false) String deviceType,
            @Parameter(description = "位号引脚") @RequestParam(required = false) String tagPin,
            @Parameter(description = "审查意见") @RequestParam(required = false) String reviewSuggestion,
            @Parameter(description = "是否通过(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed
            ) {
        try {
            List<CircuitReviewResultDetailVO> result = circuitReviewResultDetailService.getCircuitReviewResultDetailVOList(resultId, ruleName, ruleType, deviceType, tagPin, reviewSuggestion, isPassed);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("查询电路审查结果详情失败", e);
            return ResponseWrapper.fail("查询电路审查结果详情失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除电路审查结果详情", description = "根据ID删除电路审查结果详情")
    public ResponseWrapper<String> deleteCircuitReviewResultDetail(
            @Parameter(description = "电路审查结果详情ID") @PathVariable Long id) {
        try {
            boolean result = circuitReviewResultDetailService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除电路审查结果详情失败", e);
            return ResponseWrapper.fail("删除电路审查结果详情失败: " + e.getMessage());
        }
    }
} 