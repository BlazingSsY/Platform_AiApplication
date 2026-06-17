package com.starmol.logicreview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.common.ResponseWrapper;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResultDetail;
import com.starmol.logicreview.service.SourceCodeReviewResultDetailService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 逻辑审查结果详情控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/logic-review-result-detail")
@Tag(name = "逻辑审查结果详情管理", description = "逻辑审查结果详情相关接口")
public class SourceCodeReviewResultDetailController {

    @Resource
    private SourceCodeReviewResultDetailService sourceCodeReviewResultDetailService;

    @PostMapping
    @Operation(summary = "创建逻辑审查结果详情", description = "创建新的逻辑审查结果详情")
    public ResponseWrapper<SourceCodeReviewResultDetailVO> createSourceCodeReviewResultDetail(@RequestBody SourceCodeReviewResultDetail sourceCodeReviewResultDetail) {
        try {
            SourceCodeReviewResultDetailVO result = sourceCodeReviewResultDetailService.createSourceCodeReviewResultDetail(sourceCodeReviewResultDetail);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("创建逻辑审查结果详情失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新逻辑审查结果详情", description = "根据ID更新逻辑审查结果详情")
    public ResponseWrapper<SourceCodeReviewResultDetailVO> updateSourceCodeReviewResultDetail(
            @Parameter(description = "逻辑审查结果详情ID") @PathVariable Long id,
            @RequestBody SourceCodeReviewResultDetail sourceCodeReviewResultDetail) {
        try {
            SourceCodeReviewResultDetailVO result = sourceCodeReviewResultDetailService.updateSourceCodeReviewResultDetail(id, sourceCodeReviewResultDetail);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("更新逻辑审查结果详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取逻辑审查结果详情", description = "根据ID获取逻辑审查结果详情")
    public ResponseWrapper<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetail(
            @Parameter(description = "逻辑审查结果详情ID") @PathVariable Long id) {
        try {
            SourceCodeReviewResultDetailVO result = sourceCodeReviewResultDetailService.getSourceCodeReviewResultDetailVOById(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("获取逻辑审查结果详情失败: " + e.getMessage());
        }
    }


    @GetMapping("/page")
    @Operation(summary = "分页查询逻辑审查结果详情", description = "分页查询逻辑审查结果详情列表")
    public ResponseWrapper<IPage<SourceCodeReviewResultDetailVO>> getSourceCodeReviewResultDetailPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId,
            @Parameter(description = "规则名称") @RequestParam(required = false) String ruleName,
            @Parameter(description = "规则类型") @RequestParam(required = false) String ruleType,
            @Parameter(description = "源逻辑文件名") @RequestParam(required = false) String sourceFileName,
            @Parameter(description = "码文行号") @RequestParam(required = false) String lineNumber,
            @Parameter(description = "错误逻辑") @RequestParam(required = false) String errorCode,
            @Parameter(description = "错误原因") @RequestParam(required = false) String errorReason,
            @Parameter(description = "审查意见") @RequestParam(required = false) String reviewSuggestion,
            @Parameter(description = "是否通过(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed
            ) {
        try {
            Page<SourceCodeReviewResultDetailVO> page = new Page<>(pageNumber, pageSize);
            IPage<SourceCodeReviewResultDetailVO> result = sourceCodeReviewResultDetailService.getSourceCodeReviewResultDetailVOPage(page, resultId, ruleName, ruleType, sourceFileName, lineNumber, errorCode, errorReason, reviewSuggestion, isPassed);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("分页查询逻辑审查结果详情失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "查询逻辑审查结果详情", description = "查询逻辑审查结果详情列表")
    public ResponseWrapper<List<SourceCodeReviewResultDetailVO>> getSourceCodeReviewResultDetailList(
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId,
            @Parameter(description = "规则名称") @RequestParam(required = false) String ruleName,
            @Parameter(description = "规则类型") @RequestParam(required = false) String ruleType,
            @Parameter(description = "源逻辑文件名") @RequestParam(required = false) String sourceFileName,
            @Parameter(description = "码文行号") @RequestParam(required = false) String lineNumber,
            @Parameter(description = "错误逻辑") @RequestParam(required = false) String errorCode,
            @Parameter(description = "错误原因") @RequestParam(required = false) String errorReason,
            @Parameter(description = "审查意见") @RequestParam(required = false) String reviewSuggestion,
            @Parameter(description = "是否通过(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed
            ) {
        try {
            List<SourceCodeReviewResultDetailVO> result = sourceCodeReviewResultDetailService.getSourceCodeReviewResultDetailVOList(resultId, ruleName, ruleType, sourceFileName, lineNumber, errorCode, errorReason, reviewSuggestion, isPassed);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("查询逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("查询逻辑审查结果详情失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除逻辑审查结果详情", description = "根据ID删除逻辑审查结果详情")
    public ResponseWrapper<Boolean> deleteSourceCodeReviewResultDetail(
            @Parameter(description = "逻辑审查结果详情ID") @PathVariable Long id) {
        try {
            Boolean result = sourceCodeReviewResultDetailService.removeById(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("删除逻辑审查结果详情失败", e);
            return ResponseWrapper.fail("删除逻辑审查结果详情失败: " + e.getMessage());
        }
    }
} 