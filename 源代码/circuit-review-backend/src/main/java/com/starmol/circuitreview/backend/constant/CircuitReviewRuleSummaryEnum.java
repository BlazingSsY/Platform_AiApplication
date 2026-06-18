package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum CircuitReviewRuleSummaryEnum implements IEnum<Integer> {
    NOT_APPLICABLE(0, "不适用"),
    PASSED(1, "通过"),
    NOT_PASSED(2, "不通过");
    private final Integer value;
    @Getter
    private final String name;

    CircuitReviewRuleSummaryEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CircuitReviewRuleSummaryEnum getByValue(String name) {
        for (CircuitReviewRuleSummaryEnum value : CircuitReviewRuleSummaryEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
