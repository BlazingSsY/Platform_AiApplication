package com.starmol.sso.server.pojo.dto.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class PasswordUpdateDto {
    private String oldPassword;
    private String password;
}
