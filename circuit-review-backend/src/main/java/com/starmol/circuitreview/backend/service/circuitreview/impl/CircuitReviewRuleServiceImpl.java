package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRuleVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewRule;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewRuleMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewRuleService;
import com.starmol.circuitreview.backend.service.circuitreview.feign.ReviewClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 电路审查规则服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CircuitReviewRuleServiceImpl extends BaseServiceImpl<CircuitReviewRuleMapper, CircuitReviewRule> implements CircuitReviewRuleService {
    
    private final ObjectMapper objectMapper;
    
    @Autowired
    private ReviewClient reviewClient;

    @Override
    public CircuitReviewRuleVO createCircuitReviewRule(CircuitReviewRule circuitReviewRule) {
        CircuitReviewRule savedCircuitReviewRule = this.saveAndReturnObject(circuitReviewRule);
        return convertToVO(savedCircuitReviewRule);
    }

    @Override
    public CircuitReviewRuleVO updateCircuitReviewRule(Long id, CircuitReviewRule circuitReviewRule) {
        circuitReviewRule.setId(id);
        CircuitReviewRule updatedCircuitReviewRule = this.updateByIDAndReturnObject(circuitReviewRule);
        return convertToVO(updatedCircuitReviewRule);
    }

    @Override
    public CircuitReviewRuleVO getCircuitReviewRuleVOById(Long id) {
        CircuitReviewRule circuitReviewRule = this.getById(id);
        return convertToVO(circuitReviewRule);
    }

    @Override
    public CircuitReviewRuleVO getCircuitReviewRuleVOByCode(String ruleCode) {
        List<CircuitReviewRule> reviewRuleList = this.list(Wrappers.<CircuitReviewRule>lambdaQuery().eq(CircuitReviewRule::getCode, ruleCode));
        if (CollectionUtils.isNotEmpty(reviewRuleList)) {
            CircuitReviewRule circuitReviewRule = reviewRuleList.get(0);
            return convertToVO(circuitReviewRule);
        }
        else  {
            return null;
        }
    }

    @Override
    public IPage<CircuitReviewRuleVO> getCircuitReviewRuleVOPage(Page<CircuitReviewRule> page, String name) {
        IPage<CircuitReviewRule> circuitReviewRulePage = this.page(page, 
            Wrappers.<CircuitReviewRule>lambdaQuery()
                .like(StringUtils.isNotEmpty(name), CircuitReviewRule::getName, name)
        );
        return circuitReviewRulePage.convert(this::convertToVO);
    }

    @Override
    public List<CircuitReviewRuleVO> getCircuitReviewRuleVOList(RuleTypeEnum ruleTypeEnum, String ruleName) {
        List<CircuitReviewRule> reviewRuleList = this.list(Wrappers.<CircuitReviewRule>lambdaQuery()
                .isNotNull(CircuitReviewRule::getCode)
                .isNotNull(CircuitReviewRule::getName)
                .eq(CircuitReviewRule::getIsDeprecate, 0)
                .eq(Objects.nonNull(ruleTypeEnum), CircuitReviewRule::getType, ruleTypeEnum)
                .like(StringUtils.isNotEmpty(ruleName), CircuitReviewRule::getName, ruleName)
                .orderByDesc(CircuitReviewRule::getType)
                .orderByAsc(CircuitReviewRule::getCode));
        return reviewRuleList.stream().map(this::convertToVO).toList();
    }

    public void syncRulesFromExternalService() {
        try {
            log.info("开始同步规则数据...");
            
            // 调用外部服务获取规则数据
            ResponseWrapper<Map<String, Object>> response = reviewClient.getRules();
            
            if (response != null && response.getContent() != null) {
                Map<String, Object> content = response.getContent();
                Object dataObj = content.get("data");
                
                if (dataObj instanceof List<?> dataList) {
                    // 获取数据库中现有的规则，按code建立映射
                    List<CircuitReviewRule> existingRules = this.list();
                    Map<String, CircuitReviewRule> existingRulesByCode = existingRules.stream()
                            .filter(rule -> StringUtils.isNotBlank(rule.getCode()))
                            .collect(Collectors.toMap(CircuitReviewRule::getCode, rule -> rule, (existing, duplicate) -> existing));

                    int addedCount = 0;
                    int updatedCount = 0;

                    // 处理外部服务返回的每个规则
                    for (Object ruleObj : dataList) {
                        if (ruleObj instanceof Map<?, ?> ruleMap) {
                            Object nameObj = ruleMap.get("name");
                            Object codeObj = ruleMap.get("code");
                            Object typeObj = ruleMap.get("type");
                            Object isDeprecateObj = ruleMap.get("isDeprecate");

                            if (nameObj instanceof String ruleName && StringUtils.isNotBlank(ruleName) &&
                                codeObj instanceof String ruleCode && StringUtils.isNotBlank(ruleCode) &&
                                typeObj instanceof String ruleType && StringUtils.isNotBlank(ruleType)
                            ) {

                                CircuitReviewRule existingRule = existingRulesByCode.get(ruleCode);
                                if (existingRule != null) {
                                    existingRule.setName(ruleName);
                                    existingRule.setType(RuleTypeEnum.getByName(ruleType));
                                    if (isDeprecateObj instanceof String isDeprecate && StringUtils.isNotBlank(isDeprecate)) {
                                        try {
                                            existingRule.setIsDeprecate(Integer.parseInt(isDeprecate));
                                        } catch (NumberFormatException e) {
                                            log.warn("isDeprecate字段转换错误: {}", isDeprecate);
                                            existingRule.setIsDeprecate(0);
                                        }
                                    } else {
                                        // 如果isDeprecate不存在、为null或无效，默认设为0（正常状态）
                                        existingRule.setIsDeprecate(0);
                                    }
                                    this.updateById(existingRule);
                                    updatedCount++;
                                    log.debug("更新规则: {} ({})", ruleName, ruleCode);
                                } else {
                                    // 规则不存在，新增规则
                                    CircuitReviewRule newRule = new CircuitReviewRule();
                                    newRule.setName(ruleName);
                                    newRule.setCode(ruleCode);
                                    newRule.setType(RuleTypeEnum.getByName(ruleType));
                                    if (isDeprecateObj instanceof String isDeprecate && StringUtils.isNotBlank(isDeprecate)) {
                                        try {
                                            newRule.setIsDeprecate(Integer.parseInt(isDeprecate));
                                        } catch (NumberFormatException e) {
                                            log.warn("isDeprecate字段转换错误: {}", isDeprecate);
                                            newRule.setIsDeprecate(0);
                                        }
                                    } else {
                                        // 如果isDeprecate不存在、为null或无效，默认设为0（正常状态）
                                        newRule.setIsDeprecate(0);
                                    }
                                    this.save(newRule);
                                    addedCount++;
                                    log.debug("新增规则: {} ({})", ruleName, ruleCode);
                                }
                            } else {
                                log.warn("规则数据格式不正确，缺少name或code字段: {}", ruleMap);
                            }
                        }
                    }
                    
                    log.info("规则同步完成 - 新增: {}, 更新: {}", addedCount, updatedCount);
                } else {
                    log.warn("外部服务返回的数据格式不正确，data字段不是数组类型");
                }
            } else {
                log.warn("外部服务返回数据为空");
            }
            
        } catch (Exception e) {
            log.error("同步规则数据失败", e);
        }
    }

    /**
     * 将实体转换为VO
     */
    private CircuitReviewRuleVO convertToVO(CircuitReviewRule circuitReviewRule) {
        if (circuitReviewRule == null) {
            return null;
        }
        CircuitReviewRuleVO circuitReviewRuleVO = objectMapper.convertValue(circuitReviewRule, CircuitReviewRuleVO.class);
        circuitReviewRuleVO.setTypeStr(circuitReviewRule.getType().getName());
        return circuitReviewRuleVO;
    }
} 