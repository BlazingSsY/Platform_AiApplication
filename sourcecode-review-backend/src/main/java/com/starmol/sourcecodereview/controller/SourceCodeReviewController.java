package com.starmol.sourcecodereview.controller;

import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewRequestVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewStopVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 代码审查控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-review")
@Tag(name = "代码审查管理", description = "代码审查相关接口")
public class SourceCodeReviewController {

    @Resource
    private SourceCodeReviewResultService sourceCodeReviewResultService;

    @PostMapping
    @Operation(summary = "代码审查", description = "创建新的代码审查")
    public ResponseWrapper<Long> createSourceCodeReview(@RequestBody SourceCodeReviewRequestVO sourceCodeReviewRequestVO) {
        try {
            Long taskId = sourceCodeReviewResultService.submitSourceCodeReview(sourceCodeReviewRequestVO);
            return ResponseWrapper.success(taskId);
        } catch (Exception e) {
            log.error("创建代码审查结果失败", e);
            return ResponseWrapper.fail("创建代码审查结果失败: " + e.getMessage());
        }
    }


    @PostMapping("/stop")
    @Operation(summary = "停止代码审查", description = "停止代码审查")
    public ResponseWrapper<Void> stopSourceCodeReview(@RequestBody SourceCodeReviewStopVO sourceCodeReviewStopVO) {
        try {
            sourceCodeReviewResultService.stopSourceCodeReview(sourceCodeReviewStopVO);
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("停止代码审查结果失败", e);
            return ResponseWrapper.fail("停止代码审查结果失败: " + e.getMessage());
        }
    }
}
