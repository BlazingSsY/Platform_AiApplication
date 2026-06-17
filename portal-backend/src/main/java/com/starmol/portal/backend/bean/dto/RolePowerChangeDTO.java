package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "角色权限信息DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolePowerChangeDTO implements Serializable {
    private static final long serialVersionUID = 5760034272814953521L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "权限ID")
    private Long powerId;

    @Schema(description = "权限名称")
    private String powerName;

    /**
     * 节点是否选择
     */
    @Schema(description = "是否选择")
    private Boolean selected;
}