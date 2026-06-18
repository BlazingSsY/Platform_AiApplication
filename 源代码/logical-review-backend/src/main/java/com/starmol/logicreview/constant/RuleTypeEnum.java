package com.starmol.logicreview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum RuleTypeEnum implements IEnum<Integer> {
    PROPOSITIONAL_RULE(1, "建议规则"),
    NEEDFUL_RULE(2, "必要规则"),
    COERCIVE_RULE(3, "强制规则");
    private final Integer value;
    @Getter
    private final String name;

    RuleTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static RuleTypeEnum getByName(String name) {
        for (RuleTypeEnum value : RuleTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static RuleTypeEnum getByValue(Integer value) {
        for (RuleTypeEnum enumValue : RuleTypeEnum.values()) {
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
