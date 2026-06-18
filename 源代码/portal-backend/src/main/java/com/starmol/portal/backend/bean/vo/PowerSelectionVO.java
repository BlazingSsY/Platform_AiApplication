package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "可选权限信息VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerSelectionVO implements Serializable {

    @Schema(description = "父节点ID")
    Long fId;
    private Long id;
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
    /**
     * 节点是否选择
     */
    @Schema(description = "是否选择")
    private Boolean selected;
}