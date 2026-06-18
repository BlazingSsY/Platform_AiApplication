package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SsoToken implements Serializable {

	private static final long serialVersionUID = 7975415790497139550L;

	private String accessToken;
	private String refreshToken;
	private OauthUserProfile attributes;

}
