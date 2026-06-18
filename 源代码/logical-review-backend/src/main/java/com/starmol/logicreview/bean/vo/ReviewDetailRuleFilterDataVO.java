package com.starmol.logicreview.bean.vo;

import com.starmol.logicreview.constant.RuleTypeEnum;
import lombok.Data;

@Data
public class ReviewDetailRuleFilterDataVO {
    private RuleTypeEnum ruleType;
    private String reviewRule;
    private String deviceType;
    private Integer isPassed;
}
