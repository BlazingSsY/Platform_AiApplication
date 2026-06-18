package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "urm_power")
public class Power extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 5760034272814953522L;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限别名")
    private String alias;

    @Schema(description = "菜单类型")
    private String menuType;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "组件名称")
    private String component;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "是否外联")
    private Boolean isFrame;

    @Schema(description = "是否显示")
    private Boolean visible;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "同级别显示顺序")
    private Integer sequence;

    @Schema(description = "父节点ID")
    @TableField(value = "f_id")
    Long  fId;

}
