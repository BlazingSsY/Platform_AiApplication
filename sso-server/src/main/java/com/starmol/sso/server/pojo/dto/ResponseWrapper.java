package com.starmol.sso.server.pojo.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ResponseWrapper<T> implements Serializable {
    private static final long serialVersionUID = -2057997441963343533L;
    private boolean succ;
    private String msg;
    private int code;
    private T content;

    public static <T> ResponseWrapper<T> success(T object) {
        return new ResponseWrapper<T>().setCode(0).setSucc(true).setMsg("操作成功").setContent(object);
    }

    public static <T> ResponseWrapper<T> success() {
        return new ResponseWrapper<T>().setCode(0).setSucc(true).setMsg("操作成功");
    }

    public static ResponseWrapper<String> failed(String message, int code) {
        return new ResponseWrapper<String>().setMsg(message).setCode(code);
    }
}
