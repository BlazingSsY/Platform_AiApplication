package com.starmol.logicreview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.bo.CodeReviewAllRuleBO;
import com.starmol.logicreview.bean.bo.CodeReviewRuleBO;
import com.starmol.logicreview.bean.bo.SourceCodeReviewResultDetailRuleBO;
import com.starmol.logicreview.bean.dto.FilterAllRulesDTO;
import com.starmol.logicreview.bean.dto.RuleFilterDTO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResultDetail;
import com.starmol.logicreview.repository.codereview.SourceCodeReviewResultDetailMapper;
import com.starmol.logicreview.service.SourceCodeReviewResultDetailService;
import com.starmol.logicreview.service.SourceCodeReviewRuleService;
import com.starmol.logicreview.service.base.impl.BaseServiceImpl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码审查结果详情服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class SourceCodeReviewResultDetailServiceImpl extends BaseServiceImpl<SourceCodeReviewResultDetailMapper, SourceCodeReviewResultDetail> implements SourceCodeReviewResultDetailService {

    private final SourceCodeReviewRuleService sourceCodeReviewRuleService;
    private final ObjectMapper objectMapper;

    @Override
    public SourceCodeReviewResultDetailVO createSourceCodeReviewResultDetail(SourceCodeReviewResultDetail sourceCodeReviewResultDetail) {
        SourceCodeReviewResultDetail savedSourceCodeReviewResultDetail = this.saveAndReturnObject(sourceCodeReviewResultDetail);
        return convertToVO(savedSourceCodeReviewResultDetail);
    }

    @Override
    public void removeDetailByResultIdList(List<Long> resultIdList){
        this.remove(Wrappers.<SourceCodeReviewResultDetail>lambdaQuery().in(SourceCodeReviewResultDetail::getResultId, resultIdList));
    }


    @Override
    public SourceCodeReviewResultDetailVO updateSourceCodeReviewResultDetail(Long id, SourceCodeReviewResultDetail sourceCodeReviewResultDetail) {
        sourceCodeReviewResultDetail.setId(id);
        SourceCodeReviewResultDetail updatedSourceCodeReviewResultDetail = this.updateByIDAndReturnObject(sourceCodeReviewResultDetail);
        return convertToVO(updatedSourceCodeReviewResultDetail);
    }

    @Override
    public SourceCodeReviewResultDetailVO getSourceCodeReviewResultDetailVOById(Long id) {
        SourceCodeReviewResultDetail sourceCodeReviewResultDetail = this.getById(id);
        return convertToVO(sourceCodeReviewResultDetail);
    }

    @Override
    public IPage<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOPage(IPage page, Long resultId, String ruleName, String ruleType, String sourceFileName, String lineNumber, String errorCode, String errorReason, String reviewSuggestion, Integer isPassed) {
        /** TODO 现在代码审查规则不再缓存,不能用关联查询 调试完成后删除
         * Integer emptySourceFileName  = Objects.nonNull(sourceFileName) && sourceFileName.trim() == "" ? 1 : 0;
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        // ruleType为枚举，传递数据库中的int值
        return this.getBaseMapper().selectDetailWithRule(
                page,
                resultId,
                ruleName,
                ruleType,
                sourceFileName,
                lineNumber,
                errorCode,
                errorReason,
                reviewSuggestion,
                isPassed,
                emptySourceFileName
        );*/
        //调用用户接口,获取所有审查规则(根据用户提供的信息,大概200多个)
        FilterAllRulesDTO filterAllRulesDTO = new FilterAllRulesDTO();
        filterAllRulesDTO.setFilter(new RuleFilterDTO().setLanguage(Collections.emptyList()).setSelectStatus(Collections.emptyList()).setRuleType(Collections.emptyList()).setRuleSource(Collections.emptyList()).setDesc(""));
        CodeReviewAllRuleBO codeReviewRuleServiceAllRules =sourceCodeReviewRuleService.getAllRules(filterAllRulesDTO);
        List<CodeReviewRuleBO>  codeReviewRuleBOList = codeReviewRuleServiceAllRules.getRules();
        //根据ruleName, ruleType 过滤
        List<CodeReviewRuleBO>  filterCodeReviewRuleBOList = CollectionUtils.isEmpty(codeReviewRuleBOList) ? Collections.emptyList() : codeReviewRuleBOList.stream().filter(
                codeReviewRuleBO ->
                        (StringUtils.isEmpty(ruleName) || codeReviewRuleBO.getId().contains(ruleName)) &&
                        (StringUtils.isEmpty(ruleType) || codeReviewRuleBO.getRuleType().contains(ruleType))
        ).toList();
        if ((StringUtils.isNotEmpty(ruleName) || StringUtils.isNotEmpty(ruleType)) && CollectionUtils.isEmpty(filterCodeReviewRuleBOList)) {
            return new Page<>();
        }
        //获取规则以 规则id 为key的
        Map<String, CodeReviewRuleBO> codeReviewRuleBOMap = filterCodeReviewRuleBOList.stream().collect(Collectors.toMap(CodeReviewRuleBO::getId, Function.identity(), (oldValue, newValue) -> newValue));
        //获取规则id(即: ruleCode)的列表
        List<String> ruleCodeList = filterCodeReviewRuleBOList.stream().map(CodeReviewRuleBO::getId).toList();

        IPage<SourceCodeReviewResultDetail> sourceCodeReviewResultDetailListPage = this.page(page, Wrappers.<SourceCodeReviewResultDetail>lambdaQuery().eq(SourceCodeReviewResultDetail::getResultId, resultId)
                .in(CollectionUtils.isNotEmpty(ruleCodeList), SourceCodeReviewResultDetail::getRuleCode, ruleCodeList)
                .like(StringUtils.isNotEmpty(sourceFileName), SourceCodeReviewResultDetail::getSourceFileName, sourceFileName)
                .like(StringUtils.isNotEmpty(lineNumber), SourceCodeReviewResultDetail::getLineNumber, lineNumber)
                .like(StringUtils.isNotEmpty(errorCode), SourceCodeReviewResultDetail::getErrorCode, errorCode)
                .like(StringUtils.isNotEmpty(errorReason), SourceCodeReviewResultDetail::getErrorReason, errorReason)
                .like(StringUtils.isNotEmpty(reviewSuggestion), SourceCodeReviewResultDetail::getReviewSuggestion, reviewSuggestion)
                .eq(Objects.nonNull(isPassed), SourceCodeReviewResultDetail::getIsPassed, isPassed)
        );

        //添加记录需要的 审查规则的信息;
        IPage<SourceCodeReviewResultDetailVO> sourceCodeReviewResultDetailVOListPage = sourceCodeReviewResultDetailListPage.convert(sourceCodeReviewResultDetail -> {
            SourceCodeReviewResultDetailVO sourceCodeReviewResultDetailVO = objectMapper.convertValue(sourceCodeReviewResultDetail, SourceCodeReviewResultDetailVO.class);
            if (Objects.nonNull(sourceCodeReviewResultDetail.getRuleCode()) && codeReviewRuleBOMap.containsKey(sourceCodeReviewResultDetail.getRuleCode())) {
                sourceCodeReviewResultDetailVO.setRuleName(codeReviewRuleBOMap.get(sourceCodeReviewResultDetail.getRuleCode()).getDesc());
                sourceCodeReviewResultDetailVO.setRuleType(codeReviewRuleBOMap.get(sourceCodeReviewResultDetail.getRuleCode()).getRuleType());
                sourceCodeReviewResultDetailVO.setExplain(codeReviewRuleBOMap.get(sourceCodeReviewResultDetail.getRuleCode()).getExplain());
            }
            return sourceCodeReviewResultDetailVO;
        });
        return sourceCodeReviewResultDetailVOListPage;
    }

    @Override
    public List<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOList(Long resultId, String ruleName, String ruleType, String sourceFileName, String lineNumber, String errorCode, String errorReason, String reviewSuggestion, Integer isPassed) {
        Integer emptySourceFileName  = Objects.nonNull(sourceFileName) && sourceFileName.trim() == "" ? 1 : 0;
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        // ruleType为枚举，传递数据库中的int值
        return this.getBaseMapper().selectDetailWithRule(
                resultId,
                ruleName,
                ruleType,
                sourceFileName,
                lineNumber,
                errorCode,
                errorReason,
                reviewSuggestion,
                isPassed,
                emptySourceFileName
        );
    }

    @Override
    public List<SourceCodeReviewResultDetailVO> getAllDetailsByResultId(Long resultId) {
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的VO
        return this.getBaseMapper().getAllDetailsByResultId(resultId);
    }


    @Override
    public List<SourceCodeReviewResultDetailRuleBO> getAllDetailsAndRuleByResultId(Long resultId) {
        // 直接调用mapper的联合查询方法，返回带ruleType和ruleName的BO
        return this.getBaseMapper().getAllDetailsAndRuleByResultId(resultId);
    }


    @Override
    public List<String> getSourceCodeReviewResultFilters(Long id, Integer isPassed) {
        return this.getBaseMapper().getSourceCodeReviewResultFilters(id, isPassed);
    }

    @Override
    public List<String> getCircuitReviewResultRuleFilters(Long id, Integer isPassed) {
        return this.getBaseMapper().getSourceCodeReviewResultRuleFilters(id, isPassed);
    }

    /**
     * 将实体转换为VO
     */
    private SourceCodeReviewResultDetailVO convertToVO(SourceCodeReviewResultDetail sourceCodeReviewResultDetail) {
        if (sourceCodeReviewResultDetail == null) {
            return null;
        }
        return objectMapper.convertValue(sourceCodeReviewResultDetail, SourceCodeReviewResultDetailVO.class);
    }
} 