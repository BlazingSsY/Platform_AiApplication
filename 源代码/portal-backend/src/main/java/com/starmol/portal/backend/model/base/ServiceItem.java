package com.starmol.portal.backend.model.base;

import com.starmol.portal.backend.service.base.BaseService;

import java.io.Serializable;

import lombok.Data;

@Data
public class ServiceItem implements Serializable {
    /**
     * 关系字段名
     */
    private String relationFieldName;
    /**
     * CascadeService服务实例
     */
    private BaseService service;

    public ServiceItem(String relationFieldName, BaseService service) {
        this.relationFieldName = relationFieldName;
        this.service = service;
    }
}