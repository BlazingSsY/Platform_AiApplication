package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum CircuitReviewResultDetailAuditTypeEnum implements IEnum<Integer> {
    RULE_NOT_APPLICABLE(1, "规则不适用"),
    ISSUE_EXCEPTION(2, "问题可例外"),
    REVIEW_NO_IMPACT(3, "复查无影响"),
    REVIEW_INCORRECT(4, "审查不正确");
    private final Integer value;
    @Getter
    private final String name;

    CircuitReviewResultDetailAuditTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CircuitReviewResultDetailAuditTypeEnum getByName(String name) {
        for (CircuitReviewResultDetailAuditTypeEnum value : CircuitReviewResultDetailAuditTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static CircuitReviewResultDetailAuditTypeEnum getByValue(Integer value) {
        for (CircuitReviewResultDetailAuditTypeEnum enumValue : CircuitReviewResultDetailAuditTypeEnum.values()) {
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
