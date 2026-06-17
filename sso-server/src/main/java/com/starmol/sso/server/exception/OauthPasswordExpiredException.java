package com.starmol.sso.server.exception;

import com.starmol.sso.server.enums.ResponseProduceTypeEnum;
import com.starmol.sso.server.util.response.ResponseErrorEnum;

public class OauthPasswordExpiredException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;
    private int code = 500;
    private String pagePath;
    private ResponseProduceTypeEnum responseProduceTypeEnum;

    public OauthPasswordExpiredException(String message) {
        super(message);
        this.message = message;
    }

    public OauthPasswordExpiredException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public OauthPasswordExpiredException(String message, ResponseErrorEnum responseErrorEnum) {
        super(message);
        this.message = message;
        this.code = responseErrorEnum.getCode();
    }

    public OauthPasswordExpiredException(String message, ResponseProduceTypeEnum responseProduceTypeEnum) {
        super(message);
        this.message = message;
        this.responseProduceTypeEnum = responseProduceTypeEnum;
    }

    public OauthPasswordExpiredException(String message, ResponseErrorEnum responseErrorEnum, ResponseProduceTypeEnum responseProduceTypeEnum) {
        super(message);
        this.message = message;
        this.responseProduceTypeEnum = responseProduceTypeEnum;
        this.code = responseErrorEnum.getCode();
    }

    public OauthPasswordExpiredException(String message, ResponseProduceTypeEnum responseProduceTypeEnum, String pagePath) {
        super(message);
        this.message = message;
        this.pagePath = pagePath;
        this.responseProduceTypeEnum = responseProduceTypeEnum;
    }

    public OauthPasswordExpiredException(String message, ResponseErrorEnum responseErrorEnum, ResponseProduceTypeEnum responseProduceTypeEnum, String pagePath) {
        super(message);
        this.message = message;
        this.pagePath = pagePath;
        this.responseProduceTypeEnum = responseProduceTypeEnum;
        this.code = responseErrorEnum.getCode();
    }

    public OauthPasswordExpiredException(String message, ResponseErrorEnum responseErrorEnum, Throwable e) {
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

    public ResponseProduceTypeEnum getResponseProduceTypeEnum() {
        return responseProduceTypeEnum;
    }

    public void setResponseProduceTypeEnum(ResponseProduceTypeEnum responseProduceTypeEnum) {
        this.responseProduceTypeEnum = responseProduceTypeEnum;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }
}
