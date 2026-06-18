package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRuleVO;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewRule;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 电路审查规则服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitReviewRuleService extends BaseService<CircuitReviewRule> {
    
    /**
     * 创建电路审查规则并返回VO
     */
    CircuitReviewRuleVO createCircuitReviewRule(CircuitReviewRule circuitReviewRule);
    
    /**
     * 更新电路审查规则并返回VO
     */
    CircuitReviewRuleVO updateCircuitReviewRule(Long id, CircuitReviewRule circuitReviewRule);
    
    /**
     * 根据ID获取电路审查规则VO
     */
    CircuitReviewRuleVO getCircuitReviewRuleVOById(Long id);

    CircuitReviewRuleVO getCircuitReviewRuleVOByCode(String ruleCode);

    /**
     * 分页查询电路审查规则VO
     */
    IPage<CircuitReviewRuleVO> getCircuitReviewRuleVOPage(Page<CircuitReviewRule> page, String name);

    /**
     * 查询电路审查规则VO
     */
    List<CircuitReviewRuleVO> getCircuitReviewRuleVOList(RuleTypeEnum ruleTypeEnum, String ruleName);
} 