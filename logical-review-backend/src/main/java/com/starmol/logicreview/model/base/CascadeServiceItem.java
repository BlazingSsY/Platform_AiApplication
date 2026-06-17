package com.starmol.logicreview.model.base;

import com.starmol.logicreview.constant.ChildOperationSettingEnum;
import com.starmol.logicreview.service.base.BaseCascadeService;
import lombok.Data;

import java.io.Serializable;

@Data
public class CascadeServiceItem implements Serializable {
    /**
     * 操作设置(检查 / 置空 / 删除)
     */
    private ChildOperationSettingEnum operationSetting;
    /**
     * 关系字段名
     */
    private String relationFieldName;
    /**
     * CascadeService服务实例
     */
    private BaseCascadeService service;

    public CascadeServiceItem(String relationFieldName, BaseCascadeService service) {
        this(ChildOperationSettingEnum.CHECK, relationFieldName, service);
    }

    public CascadeServiceItem(ChildOperationSettingEnum operationSetting, String relationFieldName, BaseCascadeService service) {
        this.operationSetting = operationSetting;
        this.relationFieldName = relationFieldName;
        this.service = service;
    }
}