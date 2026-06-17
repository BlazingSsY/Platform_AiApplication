/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */

package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员修改指定用户密码信息DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordDTO {
    /**登录名，不可更改*/
    @Schema(description = "用户登录名")
    @NotNull(message = "登录名不能为空")
    private String loginName;
    /**新密码*/
    @Schema(description = "新密码")
    private String password;
}
