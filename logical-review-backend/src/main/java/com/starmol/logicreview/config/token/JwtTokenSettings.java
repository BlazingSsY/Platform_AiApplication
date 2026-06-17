package com.starmol.logicreview.config.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtTokenSettings {
    private Integer tokenExpirationTime = 30;
    private String tokenSigningKey = "air_secretKey" ;
    private Integer refreshTokenExpTime = 2;
}
