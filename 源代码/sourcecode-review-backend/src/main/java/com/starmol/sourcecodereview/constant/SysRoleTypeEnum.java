package com.starmol.sourcecodereview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;

import lombok.Getter;

public enum SysRoleTypeEnum implements IEnum<Integer> {
    ADMIN(1, "管理员"),
    SUPER_SUPERVISOR(2, "机载领导"),
    ORG_SUPERVISOR(3, "领导"),
    NORMAL_USER(4, "普通用户"),
    EXPERT(5, "专家");

    private final Integer value;
    @Getter
    private final String name;

    SysRoleTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SysRoleTypeEnum valOf(String name) {
        for (SysRoleTypeEnum value : SysRoleTypeEnum.values()) {
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
