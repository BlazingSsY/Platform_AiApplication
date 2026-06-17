package com.starmol.sso.server.exception;

import com.starmol.sso.server.util.response.ResponseErrorEnum;

public class OauthRestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String message;
	private int code = 500;

	public OauthRestException(String message) {
		super(message);
		this.message = message;
	}

	public OauthRestException(ResponseErrorEnum responseErrorEnum) {
		super(responseErrorEnum.getDescription());
		this.message = responseErrorEnum.getDescription();
		this.code = responseErrorEnum.getCode();
	}

	public OauthRestException(String message, Throwable e) {
		super(message, e);
		this.message = message;
	}

	public OauthRestException(String message, ResponseErrorEnum responseErrorEnum) {
		super(message);
		this.message = message;
		this.code = responseErrorEnum.getCode();
	}


	public OauthRestException(String message, ResponseErrorEnum responseErrorEnum, Throwable e) {
		super(message, e);
		this.message = message;
		this.code = responseErrorEnum.getCode();
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
