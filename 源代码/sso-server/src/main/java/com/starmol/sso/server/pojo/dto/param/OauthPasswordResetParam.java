package com.starmol.sso.server.pojo.dto.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class OauthPasswordResetParam extends OauthClientParam {
    private String username;
    private String oldpassword;
    private String password;
}
