package com.starmol.sourcecodereview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.bo.CodeReviewAllRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewDetailRuleListBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewSelectRuleBO;
import com.starmol.sourcecodereview.bean.dto.FilterAllRulesDTO;
import com.starmol.sourcecodereview.bean.vo.MetaDataDTO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewRuleVO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewRule;
import com.starmol.sourcecodereview.service.base.BaseService;

import java.util.List;

/**
 * 代码审查规则服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewRuleService extends BaseService<SourceCodeReviewRule> {

    /**
     * 创建代码审查规则
     *
     * @param sourceCodeReviewRule 代码审查规则
     * @return 代码审查规则VO
     */
    SourceCodeReviewRuleVO createSourceCodeReviewRule(SourceCodeReviewRule sourceCodeReviewRule);

    /**
     * 更新代码审查规则
     *
     * @param id 代码审查规则ID
     * @param sourceCodeReviewRule 代码审查规则
     * @return 代码审查规则VO
     */
    SourceCodeReviewRuleVO updateSourceCodeReviewRule(Long id, SourceCodeReviewRule sourceCodeReviewRule);

    /**
     * 根据ID获取代码审查规则VO
     *
     * @param id 代码审查规则ID
     * @return 代码审查规则VO
     */
    SourceCodeReviewRuleVO getSourceCodeReviewRuleVOById(Long id);

    /**
     * 分页查询代码审查规则VO
     */
    IPage<SourceCodeReviewRuleVO> getSourceCodeReviewRuleVOPage(Page<SourceCodeReviewRule> page, String name);

    /**
     * 查询代码审查规则VO
     */
    List<SourceCodeReviewRuleVO> getSourceCodeReviewRuleVOList();

    /**
     * 通过三方接口，获取所有规则
     */
    CodeReviewAllRuleBO getAllRules(FilterAllRulesDTO filterAllRulesDTO);

    /**
     * 通过三方接口，选中规则
     */
    void selectRules(CodeReviewSelectRuleBO codeReviewSelectRuleBO);

    /**
     * 通过三方接口，获取规则详情
     */
    CodeReviewDetailRuleListBO getRulesDetails(CodeReviewSelectRuleBO codeReviewSelectRuleBO);

    /**
     * 通过三方接口，获取Metadata
     */
    MetaDataDTO getMetaData();
} 