package com.starmol.sourcecodereview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.dto.CodeReviewRecheckResultSubmitDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeReviewRecheckDTO;
import com.starmol.sourcecodereview.bean.vo.CodeRecheckDetailVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileAuditVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.service.SourceCodeReviewRecheckService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码审查结果控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-review-recheck")
@Tag(name = "代码审查结果复审管理", description = "代码审查结果复审相关接口")
public class SourceCodeReviewRecheckController {

    @Resource
    private SourceCodeReviewRecheckService sourceCodeReviewRecheckService;

    @PostMapping
    @Operation(summary = "创建代码审查结果复审", description = "创建代码审查结果复审")
    public ResponseWrapper<Void> createSourceCodeReviewResult(@RequestBody SourceCodeReviewRecheckDTO sourceCodeReviewRecheckDTO) {
        try {
           sourceCodeReviewRecheckService.createSourceCodeReviewRecheck(sourceCodeReviewRecheckDTO);
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("创建代码审查结果复审失败", e);
            return ResponseWrapper.fail("创建代码审查结果复审失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询代码审查复核核", description = "分页查询代码审查复核核")
    public ResponseWrapper<IPage<SourceCodeFileAuditVO>> getCircuitReviewResultAuditVOPageForAdmin(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件名模糊查询参数") @RequestParam(required = false) String fileName,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "状态（1:未复核;2:复核中;3:已复核）") @RequestParam(required = false) Integer status) {
        try {
            IPage<SourceCodeFileAuditVO> result = sourceCodeReviewRecheckService.getCodeRecheckList(pageNumber, pageSize, fileName, depId, userId, status);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码审查结果审核失败", e);
            return ResponseWrapper.fail("分页查询代码审查结果审核失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail")
    @Operation(summary = "根据文件版本ID和版本号查看复核详情", description = "根据文件版本ID和版本号查看复核详情")
    public ResponseWrapper<CodeRecheckDetailVO> getSourceCodeReviewResultVersion(
            @Parameter(description = "文件版本ID") @RequestParam(required = false)  Long fileVersionId,
            @Parameter(description = "版本号") @RequestParam(required = false)  String version
    ) {
        try {
            CodeRecheckDetailVO result = sourceCodeReviewRecheckService.getSourceCodeReviewRecheckDetailByResultAndVersion(fileVersionId, version);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("代码审查,查看复核详情失败", e);
            return ResponseWrapper.fail("代码审查,查看复核详情失败: " + e.getMessage());
        }
    }


    @PostMapping("/result/submit")
    @Operation(summary = "复核评审结果提交", description = "复核评审结果提交")
    public ResponseWrapper<Void> sourceCodeReviewResultSubmit(@RequestBody CodeReviewRecheckResultSubmitDTO codeReviewRecheckResultSubmitDTO) {
        try {
            sourceCodeReviewRecheckService.submitReviewRecheckResults(codeReviewRecheckResultSubmitDTO);
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("复核评审结果提交失败", e);
            return ResponseWrapper.fail("复核评审结果提交失败: " + e.getMessage());
        }
    }

} 