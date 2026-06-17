package com.starmol.sourcecodereview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum CodeReviewRuleTypeEnum implements IEnum<Integer> {
    PROPOSITIONAL_RULE(1, "建议规则"),
    NEEDFUL_RULE(2, "必要规则"),
    COERCIVE_RULE(3, "强制规则");
    private final Integer value;
    @Getter
    private final String name;

    CodeReviewRuleTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CodeReviewRuleTypeEnum getByName(String name) {
        for (CodeReviewRuleTypeEnum value : CodeReviewRuleTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static CodeReviewRuleTypeEnum getByValue(Integer value) {
        for (CodeReviewRuleTypeEnum enumValue : CodeReviewRuleTypeEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
