package com.starmol.sourcecodereview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.bo.CodeReviewAllRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewDetailRuleListBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewSelectRuleBO;
import com.starmol.sourcecodereview.bean.dto.FilterAllRulesDTO;
import com.starmol.sourcecodereview.bean.vo.MetaDataDTO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewRuleVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewRule;
import com.starmol.sourcecodereview.service.SourceCodeReviewRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代码审查规则控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-review-rule")
@Tag(name = "代码审查规则管理", description = "代码审查规则相关接口")
public class SourceCodeReviewRuleController {

    @Resource
    private SourceCodeReviewRuleService sourceCodeReviewRuleService;

    @PostMapping
    @Operation(summary = "创建代码审查规则", description = "创建新的代码审查规则")
    public ResponseWrapper<SourceCodeReviewRuleVO> createSourceCodeReviewRule(@RequestBody SourceCodeReviewRule sourceCodeReviewRule) {
        try {
            SourceCodeReviewRuleVO result = sourceCodeReviewRuleService.createSourceCodeReviewRule(sourceCodeReviewRule);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建代码审查规则失败", e);
            return ResponseWrapper.fail("创建代码审查规则失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新代码审查规则", description = "根据ID更新代码审查规则")
    public ResponseWrapper<SourceCodeReviewRuleVO> updateSourceCodeReviewRule(
            @Parameter(description = "代码审查规则ID") @PathVariable Long id,
            @RequestBody SourceCodeReviewRule sourceCodeReviewRule) {
        try {
            SourceCodeReviewRuleVO result = sourceCodeReviewRuleService.updateSourceCodeReviewRule(id, sourceCodeReviewRule);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新代码审查规则失败", e);
            return ResponseWrapper.fail("更新代码审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取代码审查规则", description = "根据ID获取代码审查规则详情")
    public ResponseWrapper<SourceCodeReviewRuleVO> getSourceCodeReviewRule(
            @Parameter(description = "代码审查规则ID") @PathVariable Long id) {
        try {
            SourceCodeReviewRuleVO result = sourceCodeReviewRuleService.getSourceCodeReviewRuleVOById(id);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码审查规则失败", e);
            return ResponseWrapper.fail("获取代码审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询代码审查规则", description = "分页查询代码审查规则列表")
    public ResponseWrapper<IPage<SourceCodeReviewRuleVO>> getSourceCodeReviewRulePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "规则名称") @RequestParam(required = false) String name) {
        try {
            Page<SourceCodeReviewRule> page = new Page<>(pageNumber, pageSize);
            IPage<SourceCodeReviewRuleVO> result = sourceCodeReviewRuleService.getSourceCodeReviewRuleVOPage(page, name);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码审查规则失败", e);
            return ResponseWrapper.fail("分页查询代码审查规则失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "查询代码审查规则", description = "查询代码审查规则列表")
    public ResponseWrapper<List<SourceCodeReviewRuleVO>> getSourceCodeReviewRule() {
        try {
            List<SourceCodeReviewRuleVO> result = sourceCodeReviewRuleService.getSourceCodeReviewRuleVOList();
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("查询代码审查规则失败", e);
            return ResponseWrapper.fail("查询代码审查规则失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码审查规则", description = "根据ID删除代码审查规则")
    public ResponseWrapper<String> deleteSourceCodeReviewRule(
            @Parameter(description = "代码审查规则ID") @PathVariable Long id) {
        try {
            boolean result = sourceCodeReviewRuleService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除代码审查规则失败", e);
            return ResponseWrapper.fail("删除代码审查规则失败: " + e.getMessage());
        }
    }

    @PostMapping("/all")
    @Operation(summary = "获取所有代码审查规则", description = "获取所有代码审查规则(调用三方接口)")
    public ResponseWrapper<CodeReviewAllRuleBO> getAllRules(
            @RequestBody FilterAllRulesDTO filterAllRulesDTO
    ) {
        try {
            CodeReviewAllRuleBO result = sourceCodeReviewRuleService.getAllRules(filterAllRulesDTO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取所有代码审查规则失败", e);
            return ResponseWrapper.fail("获取所有代码审查规则失败: " + e.getMessage());
        }
    }

    @PostMapping("/select")
    @Operation(summary = "选中规则代码审查规则", description = "选中规则代码审查规则(调用三方接口)")
    public ResponseWrapper<Void> selectRules(@RequestBody CodeReviewSelectRuleBO codeReviewSelectRuleBO) {
        try {
            sourceCodeReviewRuleService.selectRules(codeReviewSelectRuleBO);
            return ResponseWrapper.success();
        } catch (Exception e) {
            log.error("选中规则代码审查规则失败", e);
            return ResponseWrapper.fail("选中规则代码审查规则失败: " + e.getMessage());
        }
    }

    @PostMapping("/details")
    @Operation(summary = "获取代码审查规则详情", description = "获取代码审查规则详情(调用三方接口)")
    public ResponseWrapper<CodeReviewDetailRuleListBO> getRulesDetails(@RequestBody CodeReviewSelectRuleBO codeReviewSelectRuleBO) {
        try {
            CodeReviewDetailRuleListBO codeReviewDetailRuleListBO = sourceCodeReviewRuleService.getRulesDetails(codeReviewSelectRuleBO);
            return ResponseWrapper.success(codeReviewDetailRuleListBO);
        } catch (Exception e) {
            log.error("获取代码审查规则详情失败", e);
            return ResponseWrapper.fail("获取代码审查规则详情失败: " + e.getMessage());
        }
    }


    @GetMapping("/metadata")
    @Operation(summary = "获取获取元数据信息", description = "获取获取元数据信息(调用三方接口)")
    public ResponseWrapper<MetaDataDTO> getRulesDetails() {
        try {
            MetaDataDTO metaData = sourceCodeReviewRuleService.getMetaData();
            return ResponseWrapper.success(metaData);
        } catch (Exception e) {
            log.error("获取获取元数据信息情失败", e);
            return ResponseWrapper.fail("获取获取元数据信息失败: " + e.getMessage());
        }
    }

} 