package com.starmol.sourcecodereview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sourcecodereview.bean.bo.CodeReviewAllRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewDetailRuleListBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRuleBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewSelectRuleBO;
import com.starmol.sourcecodereview.bean.bo.ResponseDataBO;
import com.starmol.sourcecodereview.bean.dto.FilterAllRulesDTO;
import com.starmol.sourcecodereview.bean.vo.MetaDataDTO;
import com.starmol.sourcecodereview.bean.vo.RuleSelectVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewRuleVO;
import com.starmol.sourcecodereview.exception.KnowException;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewRule;
import com.starmol.sourcecodereview.repository.codereview.SourceCodeReviewRuleMapper;
import com.starmol.sourcecodereview.service.base.impl.BaseServiceImpl;
import com.starmol.sourcecodereview.service.SourceCodeReviewRuleService;
import com.starmol.sourcecodereview.service.feign.CodeReviewClient;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码审查规则服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class SourceCodeReviewRuleServiceImpl extends BaseServiceImpl<SourceCodeReviewRuleMapper, SourceCodeReviewRule> implements SourceCodeReviewRuleService {

    private final ObjectMapper objectMapper;
    private final CodeReviewClient codeReviewClient;

    @Override
    public SourceCodeReviewRuleVO createSourceCodeReviewRule(SourceCodeReviewRule sourceCodeReviewRule) {
        SourceCodeReviewRule savedSourceCodeReviewRule = this.saveAndReturnObject(sourceCodeReviewRule);
        return convertToVO(savedSourceCodeReviewRule);
    }

    @Override
    public SourceCodeReviewRuleVO updateSourceCodeReviewRule(Long id, SourceCodeReviewRule sourceCodeReviewRule) {
        sourceCodeReviewRule.setId(id);
        SourceCodeReviewRule updatedSourceCodeReviewRule = this.updateByIDAndReturnObject(sourceCodeReviewRule);
        return convertToVO(updatedSourceCodeReviewRule);
    }

    @Override
    public SourceCodeReviewRuleVO getSourceCodeReviewRuleVOById(Long id) {
        SourceCodeReviewRule sourceCodeReviewRule = this.getById(id);
        return convertToVO(sourceCodeReviewRule);
    }

    @Override
    public IPage<SourceCodeReviewRuleVO> getSourceCodeReviewRuleVOPage(Page<SourceCodeReviewRule> page, String name) {
        IPage<SourceCodeReviewRule> sourceCodeReviewRulePage = this.page(page,
                Wrappers.<SourceCodeReviewRule>lambdaQuery()
                        .like(StringUtils.isNotEmpty(name), SourceCodeReviewRule::getName, name)
        );
        return sourceCodeReviewRulePage.convert(this::convertToVO);
    }

    @Override
    public List<SourceCodeReviewRuleVO> getSourceCodeReviewRuleVOList() {
        List<SourceCodeReviewRule> reviewRuleList = this.list();
        return reviewRuleList.stream().map(this::convertToVO).toList();
    }

    /**
     * 将实体转换为VO
     */
    private SourceCodeReviewRuleVO convertToVO(SourceCodeReviewRule sourceCodeReviewRule) {
        if (sourceCodeReviewRule == null) {
            return null;
        }
        return objectMapper.convertValue(sourceCodeReviewRule, SourceCodeReviewRuleVO.class);
    }



    /**
     * 通过三方接口，获取所有规则(即 codeReviewClient.getAllRules的封装)
     */
    @Override
    public CodeReviewAllRuleBO getAllRules(FilterAllRulesDTO filterAllRulesDTO) {
        try {
            //调用第三方接口,获所有规则
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getAllRules(filterAllRulesDTO, HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("获所有规则失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获所有规则成功");
                CodeReviewAllRuleBO codeReviewAllRuleBO = objectMapper.convertValue(responseWrapper.getData(), CodeReviewAllRuleBO.class);
                return codeReviewAllRuleBO;
            }
        } catch (Exception e) {
            log.error("获所有规则失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    /**
     * 通过三方接口，选中规则(即 codeReviewClient.selectRules的封装)
     */
    @Override
    public void selectRules(CodeReviewSelectRuleBO codeReviewSelectRuleBO) {
        try {
            //调用第三方接口,选中规则
            ResponseDataBO<Object> responseWrapper = codeReviewClient.selectRules(codeReviewSelectRuleBO, HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("选中规则,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        }
        catch (Exception e) {
            log.error("选中规则失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    /**
     * 通过三方接口，获取规则详情(即 codeReviewClient.getRulesDetails的封装)
     */
    @Override
    public CodeReviewDetailRuleListBO getRulesDetails(CodeReviewSelectRuleBO codeReviewSelectRuleBO) {
        try {
            //调用第三方接口,选中规则
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getRulesDetails(codeReviewSelectRuleBO, HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("获取规则详情,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取规则详情成功");
                CodeReviewDetailRuleListBO codeReviewDetailRuleListBO = objectMapper.convertValue(responseWrapper.getData(), CodeReviewDetailRuleListBO.class);
                return codeReviewDetailRuleListBO;
            }
        } catch (Exception e) {
            log.error("获取规则详情失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    @Override
    public MetaDataDTO getMetaData() {
        try {
            //调用第三方接口,选中规则
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getMetaData(HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败
                log.error("获取元数据信息,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取元数据信息成功");
                MetaDataDTO metaDataDTO = objectMapper.convertValue(responseWrapper.getData(), MetaDataDTO.class);
                return metaDataDTO;
            }
        } catch (Exception e) {
            log.error("获取元数据信息失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

} 