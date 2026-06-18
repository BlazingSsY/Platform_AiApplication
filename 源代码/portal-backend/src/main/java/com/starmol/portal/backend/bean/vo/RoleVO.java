package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "可选角色信息VO")
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleVO implements Serializable {

    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色类型")
    private String type;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "是否可编辑")
    private Boolean isEditable;

    /**
     * 是否选择
     */
    @Schema(description = "是否选择")
    private Boolean selected;
}