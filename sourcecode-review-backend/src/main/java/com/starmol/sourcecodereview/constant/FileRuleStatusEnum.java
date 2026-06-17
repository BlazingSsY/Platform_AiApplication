package com.starmol.sourcecodereview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum FileRuleStatusEnum implements IEnum<Integer> {

    NO_VIOLATION(1, "没有违反次规则"),
    VIOLATION(2, "有违反规则"),
    IN_PROGRESS(3, "正在审校中");

    private final Integer value;
    @Getter
    private final String name;

    FileRuleStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
