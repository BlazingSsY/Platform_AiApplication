package com.starmol.circuitreview.backend.bean.vo;

import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import lombok.Data;

@Data
public class ReviewDetailRuleFilterDataVO {
    private RuleTypeEnum ruleType;
    private String reviewRule;
    private String deviceType;
    private Integer isPassed;
}
