package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRuleVO;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewRule;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewRuleService;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电路审查规则控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/circuit-review-rule")
@Tag(name = "电路审查规则管理", description = "电路审查规则相关接口")
public class CircuitReviewRuleController {

    @Autowired
    private CircuitReviewRuleService circuitReviewRuleService;

    @PostMapping
    @Operation(summary = "创建电路审查规则", description = "创建新的电路审查规则")
    public ResponseWrapper<CircuitReviewRuleVO> createCircuitReviewRule(@RequestBody CircuitReviewRule circuitReviewRule) {
        try {
            CircuitReviewRuleVO result = circuitReviewRuleService.createCircuitReviewRule(circuitReviewRule);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路审查规则失败", e);
            return ResponseWrapper.fail("创建电路审查规则失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新电路审查规则", description = "根据ID更新电路审查规则")
    public ResponseWrapper<CircuitReviewRuleVO> updateCircuitReviewRule(
            @Parameter(description = "电路审查规则ID") @PathVariable Long id,
            @RequestBody CircuitReviewRule circuitReviewRule) {
        try {
            CircuitReviewRuleVO result = circuitReviewRuleService.updateCircuitReviewRule(id, circuitReviewRule);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新电路审查规则失败", e);
            return ResponseWrapper.fail("更新电路审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路审查规则", description = "根据ID获取电路审查规则详情")
    public ResponseWrapper<CircuitReviewRuleVO> getCircuitReviewRule(
            @Parameter(description = "电路审查规则ID") @PathVariable Long id) {
        try {
            CircuitReviewRuleVO result = circuitReviewRuleService.getCircuitReviewRuleVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("电路审查规则不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查规则失败", e);
            return ResponseWrapper.fail("获取电路审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询电路审查规则", description = "分页查询电路审查规则列表")
    public ResponseWrapper<IPage<CircuitReviewRuleVO>> getCircuitReviewRulePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "规则名称") @RequestParam(required = false) String name) {
        try {
            Page<CircuitReviewRule> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitReviewRuleVO> result = circuitReviewRuleService.getCircuitReviewRuleVOPage(page, name);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路审查规则失败", e);
            return ResponseWrapper.fail("分页查询电路审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "查询电路审查规则", description = "查询电路审查规则列表")
    public ResponseWrapper<List<CircuitReviewRuleVO>> getCircuitReviewRule(@RequestParam(required = false) RuleTypeEnum ruleTypeEnum, @RequestParam(required = false) String ruleName) {
        try {
            List<CircuitReviewRuleVO> result = circuitReviewRuleService.getCircuitReviewRuleVOList(ruleTypeEnum, ruleName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("查询电路审查规则失败", e);
            return ResponseWrapper.fail("查询电路审查规则失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除电路审查规则", description = "根据ID删除电路审查规则")
    public ResponseWrapper<String> deleteCircuitReviewRule(
            @Parameter(description = "电路审查规则ID") @PathVariable Long id) {
        try {
            boolean result = circuitReviewRuleService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除电路审查规则失败", e);
            return ResponseWrapper.fail("删除电路审查规则失败: " + e.getMessage());
        }
    }
} 