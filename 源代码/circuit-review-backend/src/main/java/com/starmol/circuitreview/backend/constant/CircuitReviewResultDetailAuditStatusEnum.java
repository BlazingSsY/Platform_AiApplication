package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum CircuitReviewResultDetailAuditStatusEnum implements IEnum<Integer> {
    IN_PROCESS(1, "审核中"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核拒绝"),
    CANCELLED(4, "审核取消");
    private final Integer value;
    @Getter
    private final String name;

    CircuitReviewResultDetailAuditStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static CircuitReviewResultDetailAuditStatusEnum getByName(String name) {
        for (CircuitReviewResultDetailAuditStatusEnum value : CircuitReviewResultDetailAuditStatusEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static CircuitReviewResultDetailAuditStatusEnum getByValue(Integer value) {
        for (CircuitReviewResultDetailAuditStatusEnum enumValue : CircuitReviewResultDetailAuditStatusEnum.values()) {
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
