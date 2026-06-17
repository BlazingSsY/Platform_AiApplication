package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class SsoTokenRequest {
	private String code;

	@JsonProperty("grant_type")
	private String grantType;

	@JsonProperty("client_id")
	private String clientId;

	@JsonProperty("client_secret")
	private String clientSecret;

	@JsonProperty("redirect_uri")
	private String redirectUri;
}
