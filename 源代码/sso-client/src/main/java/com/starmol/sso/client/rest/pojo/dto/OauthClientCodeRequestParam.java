package com.starmol.sso.client.rest.pojo.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class OauthClientCodeRequestParam implements Serializable {

	private static final long serialVersionUID = -4655506034165794098L;

	@NotBlank(message = "code 不能为空")
	private String code;

	@NotBlank(message = "redirectUri 不能为空")
	private String redirectUri;


}
