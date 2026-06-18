package com.starmol.sso.client.rest.pojo.bo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SsoResponse<T> {

	private int status;
	private T response;

}
