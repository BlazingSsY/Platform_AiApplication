package com.starmol.circuitreview.backend.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SelfTreeOperationSettingEnum {

    CHECK("检查", 0),
    DELETE("删除", 2);

    private String name;
    private Integer value;

    SelfTreeOperationSettingEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 枚举返回下拉选项列表
     *
     * @return List 下来列表值对象
     */
    public static List<Map<String, String>> getList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (SelfTreeOperationSettingEnum iterate : SelfTreeOperationSettingEnum.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("label", iterate.name);
            map.put("value", iterate.value.toString());
            list.add(map);
        }
        return list;
    }

    public static String getName(Integer index) {
        for (SelfTreeOperationSettingEnum iterate : SelfTreeOperationSettingEnum.values()) {
            if (iterate.value.equals(index)) {
                return iterate.name;
            }
        }
        return "";
    }

    public static Integer getValue(String name) {
        for (SelfTreeOperationSettingEnum iterate : SelfTreeOperationSettingEnum.values()) {
            if (iterate.name.equals(name)) {
                return iterate.value;
            }
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public boolean equals(ChildOperationSettingEnum obj) {
        return this.getValue().equals(obj.getValue());
    }
}
