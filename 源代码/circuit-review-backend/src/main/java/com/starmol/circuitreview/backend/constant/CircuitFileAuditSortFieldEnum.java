package com.starmol.circuitreview.backend.constant;

import lombok.Getter;

public enum CircuitFileAuditSortFieldEnum {
    FILE_NAME("fileName", "文件名"),
    DEPARTMENT_NAME("departmentName", "部门名称"),
    OWNER_NAME("ownerName", "负责人姓名"),
    STATUS("status", "状态"),
    AUDIT_TIME("auditTime", "复核时间"),
    DIAGRAM_NUMBER("diagramNumber", "电路原理图号"),
    FILE_VERSION_NAME("fileVersionName", "文件版本名称");

    @Getter
    private final String fieldName;
    @Getter
    private final String description;

    CircuitFileAuditSortFieldEnum(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 根据字段名获取枚举
     */
    public static CircuitFileAuditSortFieldEnum fromFieldName(String fieldName) {
        for (CircuitFileAuditSortFieldEnum field : values()) {
            if (field.getFieldName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
