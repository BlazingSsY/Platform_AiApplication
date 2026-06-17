package com.starmol.sso.server.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 604800s = 7 天 86400s = 24 小时 43200s = 12 小时
 */
@Setter
@Getter
@ToString
@Component
@ConfigurationProperties(prefix = "sso.oauth")
public class OauthProperties {

    private String errorUriMsg = "Error.";
    private Integer nodeNumber = 10;
    private Boolean tgcCookieSecure = true;
    private Integer rememberMeMaxTimeToLiveInSeconds = 604800;
    private Integer codeMaxTimeToLiveInSeconds = 120;
    private Integer accessTokenMaxTimeToLiveInSeconds = 43200;
    private Integer refreshTokenMaxTimeToLiveInSeconds = 86400;
    private Integer tgcAndUserInfoMaxTimeToLiveInSeconds = 86400;
    private String managementUrl;
    private String tokenSignKey;

}
