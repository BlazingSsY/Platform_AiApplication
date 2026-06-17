package com.starmol.sso.server.pojo.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OauthIntrospect implements Serializable {

    private static final long serialVersionUID = 3000085116559990818L;

    private String tokenType;
    private String grantType;
    private String clientId;
    private Long exp;
    private Long iat;

}
