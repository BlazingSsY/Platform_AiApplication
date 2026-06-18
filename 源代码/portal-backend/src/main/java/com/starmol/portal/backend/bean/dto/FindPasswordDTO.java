package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "用户登录信息DTO")
public class FindPasswordDTO {

    @Schema(description = "验证表单")
    private UserLoginDTO userLoginDTO;

    @Schema(description = "重设密码")
    private String newPassword;
}
