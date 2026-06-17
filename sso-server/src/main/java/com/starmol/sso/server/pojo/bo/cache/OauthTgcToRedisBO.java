package com.starmol.sso.server.pojo.bo.cache;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OauthTgcToRedisBO implements Serializable {

    private static final long serialVersionUID = 41535419894343419L;

    private String userInfoRedisKey;
    private Long iat;

    private Boolean boolIsRememberMe;
    private Boolean boolIsMobile;

    private String userAgent;
    private String requestIp;

}
