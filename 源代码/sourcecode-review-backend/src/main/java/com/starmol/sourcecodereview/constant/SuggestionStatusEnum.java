package com.starmol.sourcecodereview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum SuggestionStatusEnum implements IEnum<Integer> {
    NEW_OPEN(0, "新建"),
    OPEN(1, "打开"),
    REOPEN(2, "重新打开"),
    CLOSED(3, "关闭");

    private final Integer value;
    @Getter
    private final String name;

    SuggestionStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
