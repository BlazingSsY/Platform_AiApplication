package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum AuditActionTypeEnum implements IEnum<Integer> {
    APPROVE(1, "通过"),
    REJECT(2, "拒绝");
    private final Integer value;
    @Getter
    private final String name;

    AuditActionTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static AuditActionTypeEnum getByName(String name) {
        for (AuditActionTypeEnum value : AuditActionTypeEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static AuditActionTypeEnum getByValue(Integer value) {
        for (AuditActionTypeEnum enumValue : AuditActionTypeEnum.values()) {
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
