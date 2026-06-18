package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultFilterVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRuleSummaryItemVO;
import com.starmol.circuitreview.backend.bean.vo.RegenerateCircuitReviewResultRequestVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultService;
import com.starmol.circuitreview.backend.service.circuitreview.RegenerateReviewResultService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 电路审查结果控制器
 *
 * @author system
 * @since 2025-01-07
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/circuit-review-result")
@Tag(name = "电路审查结果管理", description = "电路审查结果相关接口")
public class CircuitReviewResultController {

    private final CircuitReviewResultService circuitReviewResultService;
    private final RegenerateReviewResultService regenerateReviewResultService;

    @PostMapping
    @Operation(summary = "创建电路审查结果", description = "创建新的电路审查结果")
    public ResponseWrapper<CircuitReviewResultVO> createCircuitReviewResult(@RequestBody CircuitReviewResult circuitReviewResult) {
        try {
            CircuitReviewResultVO result = circuitReviewResultService.createCircuitReviewResult(circuitReviewResult);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路审查结果失败", e);
            return ResponseWrapper.fail("创建电路审查结果失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新电路审查结果", description = "根据ID更新电路审查结果")
    public ResponseWrapper<CircuitReviewResultVO> updateCircuitReviewResult(
            @Parameter(description = "电路审查结果ID") @PathVariable Long id,
            @RequestBody CircuitReviewResult circuitReviewResult) {
        try {
            CircuitReviewResultVO result = circuitReviewResultService.updateCircuitReviewResult(id, circuitReviewResult);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新电路审查结果失败", e);
            return ResponseWrapper.fail("更新电路审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路审查结果", description = "根据ID获取电路审查结果详情")
    public ResponseWrapper<CircuitReviewResultVO> getCircuitReviewResult(
            @Parameter(description = "电路审查结果ID") @PathVariable Long id) {
        try {
            CircuitReviewResultVO result = circuitReviewResultService.getCircuitReviewResultVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("电路审查结果不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查结果失败", e);
            return ResponseWrapper.fail("获取电路审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/filters")
    @Operation(summary = "获取电路审查结果筛选条件", description = "获取电路审查结果筛选条件")
    public ResponseWrapper<CircuitReviewResultFilterVO> getCircuitReviewResultFilters(
            @Parameter(description = "电路审查结果ID") @PathVariable Long id, @Parameter(description = "规则类型") @RequestParam(required = false) RuleTypeEnum ruleType, @Parameter(description = "审查规则") @RequestParam(required = false) String reviewRule, @Parameter(description = "器件型号") @RequestParam(required = false) String deviceType, @Parameter(description = "是否通过标识(0:未通过;1:通过)") @RequestParam(required = false) Integer isPassed) {
        try {
            CircuitReviewResultFilterVO result = circuitReviewResultService.getCircuitReviewResultFilters(id, Objects.nonNull(ruleType) ? ruleType.getValue() : null, reviewRule, deviceType, isPassed);
            if (result == null) {
                return ResponseWrapper.fail("电路审查结果不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查结果筛选条件失败", e);
            return ResponseWrapper.fail("获取电路审查结果筛选条件失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询电路审查结果", description = "分页查询电路审查结果列表")
    public ResponseWrapper<IPage<CircuitReviewResultVO>> getCircuitReviewResultPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件ID") @RequestParam(required = false) Long fileId) {
        try {
            Page<CircuitReviewResult> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitReviewResultVO> result = circuitReviewResultService.getCircuitReviewResultVOPage(page, fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/rule-summary")
    @Operation(summary = "获取审查规则情况", description = "获取审查规则情况")
    public ResponseWrapper<List<CircuitReviewRuleSummaryItemVO>> getCircuitReviewRuleSummary(
            @Parameter(description = "ID") @RequestParam(required = false) Long id) {
        try {
            List<CircuitReviewRuleSummaryItemVO> result = circuitReviewResultService.getCircuitReviewRuleSummary(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查结果失败", e);
            return ResponseWrapper.fail("分页查询电路审查结果失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除电路审查结果", description = "根据ID删除电路审查结果")
    public ResponseWrapper<String> deleteCircuitReviewResult(
            @Parameter(description = "电路审查结果ID") @PathVariable Long id) {
        try {
            boolean result = circuitReviewResultService.deleteCircuitReviewResult(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除电路审查结果失败", e);
            return ResponseWrapper.fail("删除电路审查结果失败: " + e.getMessage());
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
            circuitReviewResultService.removeResultByFileId(removeObject.getId());
            wrapper.setSucc(true);
            wrapper.setMsg("删除文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error("Remove CircuitFile error:{}", e.getMessage(), e);
        }
        return wrapper;
    }

    @PostMapping("regenerate")
    @Operation(summary = "重新生成电路审查结果", description = "重新生成电路审查结果")
    public ResponseWrapper<Void> regenerateCircuitReviewResult(@RequestBody RegenerateCircuitReviewResultRequestVO request) {
        try {
            regenerateReviewResultService.regenerateReviewResult(request.getTargetParam(), request.getDepartments());
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("重新生成电路审查结果失败", e);
            return ResponseWrapper.fail("重新生成电路审查结果失败: " + e.getMessage());
        }
    }

} 