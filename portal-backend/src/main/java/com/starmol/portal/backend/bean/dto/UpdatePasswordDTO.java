package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改密码信息DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePasswordDTO {
    /**
     * 登录名，不可更改
     */
    @Schema(description = "用户登录名")
    private String loginName;

    /**
     * 旧密码
     */
    @Schema(description = "旧密码")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码")
    private String password;
}
