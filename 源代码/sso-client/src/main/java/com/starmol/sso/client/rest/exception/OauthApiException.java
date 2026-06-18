package com.starmol.sso.client.rest.exception;

public class OauthApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OauthApiException(String message) {
		super(message);
	}

	public OauthApiException(String message, Throwable e) {
		super(message, e);
	}

}
