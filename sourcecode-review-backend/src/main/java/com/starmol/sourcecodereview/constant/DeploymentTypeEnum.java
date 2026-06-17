package com.starmol.sourcecodereview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum DeploymentTypeEnum implements IEnum<Integer> {
    JI_ZAI(0, "机载"),
    ACTRI(1, "631");

    private final Integer value;
    @Getter
    private final String name;

    DeploymentTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static DeploymentTypeEnum getByValue(Integer value) {
        for (DeploymentTypeEnum enumValue : DeploymentTypeEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }

    public static DeploymentTypeEnum getByValue(String value) {
        for (DeploymentTypeEnum enumValue : DeploymentTypeEnum.values()) {
            if (enumValue.getValue().toString().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }
}
