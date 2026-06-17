package com.starmol.sso.client.rest.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author huguojun
 */
@Setter
@Getter
@Accessors(chain = true)
public class OauthTokenResponse {
	private String redirectUri;
	private String token;
	private OauthUserProfile profile;
}
