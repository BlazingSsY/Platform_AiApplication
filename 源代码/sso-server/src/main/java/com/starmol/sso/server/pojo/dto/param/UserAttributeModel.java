package com.starmol.sso.server.pojo.dto.param;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author huguojun
 */
@Setter
@Getter
@Accessors(chain = true)
public class UserAttributeModel {
    private String username;
    private String password;
    private String oldPassword;
    private String newest;
    private boolean passwordExpired;
}
