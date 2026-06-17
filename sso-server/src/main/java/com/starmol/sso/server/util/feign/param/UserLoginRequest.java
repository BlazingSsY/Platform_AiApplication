package com.starmol.sso.server.util.feign.param;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserLoginRequest {
    private String loginName;
    private String password;
    private String clientId;
    private String captcha;
}
