//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.starmol.sso.client.rest.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Setter
@Getter
public class JwtTokenSettings {
	private Integer tokenExpirationTime;
	private String tokenSigningKey;
	private Integer refreshTokenExpTime;

}
