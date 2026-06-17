package com.starmol.sso.server.pojo.dto.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class OauthFormLoginParam extends OauthAuthorizeParam {
    private String username;
    private String password;
    private String captcha;
    private Boolean boolIsRememberMe;
    private String oldPassword;

    private boolean boolIsUpdatePwd;
}
