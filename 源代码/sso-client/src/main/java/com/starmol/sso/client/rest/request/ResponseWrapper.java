package com.starmol.sso.client.rest.request;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
public class ResponseWrapper<T> implements Serializable {
    private static final long serialVersionUID = -2057997441963343533L;
    private static final String SUCCESS_MSG = "操作成功";
    private static final String FAIL_MSG = "操作失败";
    private static final Integer SUCCESS_CODE = 0;

    private boolean succ;
    private String msg;
    private T content;
    private Integer code;

    public static <T> ResponseWrapper<T> success(String msg, T object) {
        return new ResponseWrapper<T>().setSucc(true).setMsg(msg).setContent(object).setCode(SUCCESS_CODE);
    }

    public static <T> ResponseWrapper<T> success(T object) {
        return new ResponseWrapper<T>().setSucc(true).setMsg(SUCCESS_MSG).setContent(object).setCode(SUCCESS_CODE);
    }

    public static <T> ResponseWrapper<T> success(String msg) {
        return new ResponseWrapper<T>().setSucc(true).setMsg(msg).setCode(SUCCESS_CODE);
    }

    public static <T> ResponseWrapper<T> success() {
        return success(SUCCESS_MSG);
    }

    public static <T> ResponseWrapper<T> fail() {
        return fail(FAIL_MSG);
    }

    public static <T> ResponseWrapper<T> fail(String msg) {
        return new ResponseWrapper<T>().setSucc(false).setMsg(msg);
    }


}
