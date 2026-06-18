package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum FileSecretLevelEnum implements IEnum<Integer> {
    TOP_SECRET(1, "内部"),
    CONFIDENTIAL(2, "受控"),
    SECRET(3, "商密"),
    PUBLIC(4, "公开");

    private final Integer value;
    @Getter
    private final String name;

    FileSecretLevelEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
