package com.starmol.sourcecodereview.bean.vo;

import com.starmol.sourcecodereview.constant.RuleTypeEnum;
import lombok.Data;

@Data
public class ReviewDetailRuleFilterDataVO {
    private RuleTypeEnum ruleType;
    private String reviewRule;
    private String deviceType;
    private Integer isPassed;
}
