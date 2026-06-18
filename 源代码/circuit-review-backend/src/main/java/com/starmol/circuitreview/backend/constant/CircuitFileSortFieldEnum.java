package com.starmol.circuitreview.backend.constant;

import lombok.Getter;

/**
 * 电路图文件排序字段枚举
 *
 * @author system
 * @date 2025-01-07
 */
public enum CircuitFileSortFieldEnum {
    FILE_NAME("fileName", "文件名"),
    DEPARTMENT_NAME("departmentName", "部门名称"),
    OWNER_NAME("ownerName", "负责人姓名"),
    CHECK_POINTS("checkPoints", "检查点数量"),
    PASS_CHECK_POINTS("passCheckPoints", "通过检查点数量"),
    FAIL_CHECK_POINTS("failCheckPoints", "不通过检查点数量"),
    PASS_RATE("passRate", "通过率"),
    REVIEW_TIME("reviewTime", "审查时间"),
    COMPATIBLE_MODELS("compatibleModels", "配套机型"),
    PRODUCT_MODEL("productModel", "产品型号"),
    PRODUCT_NAME("productName", "产品名称"),
    DIAGRAM_NUMBER("diagramNumber", "电路原理图号"),
    DIAGRAM_VERSION("diagramVersion", "版本");

    @Getter
    private final String fieldName;
    @Getter
    private final String description;

    CircuitFileSortFieldEnum(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 根据字段名获取枚举
     */
    public static CircuitFileSortFieldEnum fromFieldName(String fieldName) {
        for (CircuitFileSortFieldEnum field : values()) {
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
