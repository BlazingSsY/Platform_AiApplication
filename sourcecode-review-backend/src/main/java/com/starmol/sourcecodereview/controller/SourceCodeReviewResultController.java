package com.starmol.sourcecodereview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.dto.SourceCodeDTO;
import com.starmol.sourcecodereview.bean.vo.CodeReviewResultVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeContentVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewProcessVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewResultFilterVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewResultVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewRuleSummaryItemVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewWaitTaskVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewResult;
import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;
import com.starmol.sourcecodereview.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代码审查结果控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-review-result")
@Tag(name = "代码审查结果管理", description = "代码审查结果相关接口")
public class SourceCodeReviewResultController {

    @Resource
    private SourceCodeReviewResultService sourceCodeReviewResultService;

    @PostMapping
    @Operation(summary = "创建代码审查结果", description = "创建新的代码审查结果")
    public ResponseWrapper<SourceCodeReviewResultVO> createSourceCodeReviewResult(@RequestBody SourceCodeReviewResult sourceCodeReviewResult) {
        try {
            SourceCodeReviewResultVO result = sourceCodeReviewResultService.createSourceCodeReviewResult(sourceCodeReviewResult);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建代码审查结果失败", e);
            return ResponseWrapper.fail("创建代码审查结果失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新代码审查结果", description = "根据ID更新代码审查结果")
    public ResponseWrapper<SourceCodeReviewResultVO> updateSourceCodeReviewResult(
            @Parameter(description = "代码审查结果ID") @PathVariable Long id,
            @RequestBody SourceCodeReviewResult sourceCodeReviewResult) {
        try {
            SourceCodeReviewResultVO result = sourceCodeReviewResultService.updateSourceCodeReviewResult(id, sourceCodeReviewResult);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新代码审查结果失败", e);
            return ResponseWrapper.fail("更新代码审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取代码审查结果", description = "根据ID获取代码审查结果详情")
    public ResponseWrapper<SourceCodeReviewResultVO> getSourceCodeReviewResult(
            @Parameter(description = "代码审查结果ID") @PathVariable Long id) {
        try {
            SourceCodeReviewResultVO result = sourceCodeReviewResultService.getSourceCodeReviewResultVOById(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码审查结果失败", e);
            return ResponseWrapper.fail("获取代码审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/list")
    @Operation(summary = "根据审查结果ID查询代码审查结果详情", description = "根据审查结果ID查询代码审查结果详情")
    public ResponseWrapper<List<SourceCodeReviewResultDetailVO>> getSourceCodeReviewResultDetailList(
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId
    ) {
        try {
            List<SourceCodeReviewResultDetailVO> result = sourceCodeReviewResultService.getSourceCodeReviewResultDetailVOAllListByResult(resultId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码审查结果详情失败", e);
            return ResponseWrapper.fail("分页查询代码审查结果详情失败: " + e.getMessage());
        }
    }


    @GetMapping("/detail/version")
    @Operation(summary = "根据审查结果ID和版本号查询代码审查结果详情", description = "根据审查结果ID和版本号查询代码审查结果详情")
    public ResponseWrapper<CodeReviewResultVO> getSourceCodeReviewResultVersion(
            @Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId,
            @Parameter(description = "版本号") @RequestParam(required = false)  String version
    ) {
        try {
            CodeReviewResultVO result = sourceCodeReviewResultService.getSourceCodeReviewResultByResultAndVersion(resultId, version);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码审查结果详情失败", e);
            return ResponseWrapper.fail("获取代码审查结果详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/process/{id}")
    @Operation(summary = "获取代码审查进度", description = "根据ID获取代码审查进度")
    public ResponseWrapper<SourceCodeReviewProcessVO> getSourceCodeReviewProcess(
            @Parameter(description = "代码审查结果ID") @PathVariable Long id) {
        try {
            SourceCodeReviewProcessVO result = sourceCodeReviewResultService.getSourceCodeReviewProcessResultVOById(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码审查进度失败", e);
            return ResponseWrapper.fail("获取代码审查进度失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/filters")
    @Operation(summary = "获取代码审查结果筛选条件", description = "获取代码审查结果筛选条件")
    public ResponseWrapper<SourceCodeReviewResultFilterVO> getSourceCodeReviewResultFilters(
            @Parameter(description = "代码审查结果ID") @PathVariable Long id, @Parameter(description = "是否通过标识(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed) {
        try {
            SourceCodeReviewResultFilterVO result = sourceCodeReviewResultService.getSourceCodeReviewResultFilters(id, isPassed);
            if (result == null) {
                return ResponseWrapper.fail("代码审查结果不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码审查结果筛选条件失败", e);
            return ResponseWrapper.fail("获取代码审查结果筛选条件失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询代码审查结果", description = "分页查询代码审查结果列表")
    public ResponseWrapper<IPage<SourceCodeReviewResultVO>> getSourceCodeReviewResultPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件ID") @RequestParam(required = false) Long fileId) {
        try {
            Page<SourceCodeReviewResult> page = new Page<>(pageNumber, pageSize);
            IPage<SourceCodeReviewResultVO> result = sourceCodeReviewResultService.getSourceCodeReviewResultVOPage(page, fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码审查结果失败", e);
            return ResponseWrapper.fail("分页查询代码审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/rule-summary")
    @Operation(summary = "获取审查规则情况", description = "获取审查规则情况")
    public ResponseWrapper<List<SourceCodeReviewRuleSummaryItemVO>> getSourceCodeReviewRuleSummary(
            @Parameter(description = "ID") @RequestParam(required = false) Long id) {
        try {
            List<SourceCodeReviewRuleSummaryItemVO> result = sourceCodeReviewResultService.getSourceCodeReviewRuleSummary(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码审查结果失败", e);
            return ResponseWrapper.fail("分页查询代码审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/wait-task")
    @Operation(summary = "获取任务等待情况", description = "获取任务等待情况")
    public ResponseWrapper<SourceCodeReviewWaitTaskVO> getWaitTask(
            @Parameter(description = "ID") @RequestParam(required = false) Long id) {
        try {
            SourceCodeReviewWaitTaskVO result = sourceCodeReviewResultService.getWaitTask(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取任务等待情况失败", e);
            return ResponseWrapper.fail("获取任务等待情况失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码审查结果", description = "根据ID删除代码审查结果")
    public ResponseWrapper<String> deleteSourceCodeReviewResult(
            @Parameter(description = "代码审查结果ID") @PathVariable Long id) {
        try {
            boolean result = sourceCodeReviewResultService.deleteSourceCodeReviewResult(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除代码审查结果失败", e);
            return ResponseWrapper.fail("删除代码审查结果失败: " + e.getMessage());
        }
    }

    /**
     * 根据文件ID删除所有审查结果
     */
    @Operation(summary = "根据文件ID删除所有审查结果", description = "根据文件ID删除所有审查结果")
    @PatchMapping("/fileId")
    public ResponseWrapper<String> removeAllByFileID(@RequestBody DeleteDTO removeObject) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        try {
            sourceCodeReviewResultService.removeResultByFileId(removeObject.getId());
            wrapper.setSucc(true);
            wrapper.setMsg("删除文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove SourceCodeFile error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    @GetMapping("/source/code")
    @Operation(summary = "获取文件源代码", description = "获取文件源代码")
    public ResponseWrapper<SourceCodeContentVO> getSourceCodeContent(
            @RequestParam(required = true) Long resultId,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String offset,
            @RequestParam(required = false) String version) {
        try {
            SourceCodeContentVO result = sourceCodeReviewResultService.getSourceCodeContent(resultId, fileName, offset, version);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取文件源代码失败", e);
            return ResponseWrapper.fail("获取文件源代码失败: " + e.getMessage());
        }
    }


    @GetMapping("/source/versions")
    @Operation(summary = "获取代码版本列表", description = "获取代码版本列表")
    public ResponseWrapper<List<String>> getLogsData(@Parameter(description = "审查结果ID") @RequestParam(required = false)  Long resultId) {
        try {
            List<String> result = sourceCodeReviewResultService.getVersionByResultId(resultId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取log数据失败", e);
            return ResponseWrapper.fail("获取log数据失败: " + e.getMessage());
        }
    }
} 