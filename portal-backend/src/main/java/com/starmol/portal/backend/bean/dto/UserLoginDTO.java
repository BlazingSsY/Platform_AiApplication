package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "用户登录信息DTO")
public class UserLoginDTO {

    /**
     * 登录名，不可更改
     */
    @Schema(description = "登录名")
    private String loginName;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "手机验证码")
    private String smsCode;

    @Schema(description = "clientId")
    private String clientId;
}
