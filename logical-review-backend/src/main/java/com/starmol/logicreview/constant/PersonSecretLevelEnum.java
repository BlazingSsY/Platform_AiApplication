package com.starmol.logicreview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum PersonSecretLevelEnum implements IEnum<Integer> {
    TOP_SECRET(1, "核心涉密"),
    CONFIDENTIAL(2, "重要涉密"),
    SECRET(3, "一般涉密"),
    PUBLIC(4, "非密");

    private final Integer value;
    @Getter
    private final String name;

    PersonSecretLevelEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
