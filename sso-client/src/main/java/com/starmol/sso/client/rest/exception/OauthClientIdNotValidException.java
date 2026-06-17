package com.starmol.sso.client.rest.exception;

public class OauthClientIdNotValidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OauthClientIdNotValidException(String message) {
		super(message);
	}

	public OauthClientIdNotValidException(String message, Throwable e) {
		super(message, e);
	}

}
