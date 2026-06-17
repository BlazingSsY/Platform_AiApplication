package com.starmol.portal.backend.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> implements Serializable {

    private static final long serialVersionUID = -2057997441963343533L;

    private static final String DEF_SUCC_MSG = "操作成功";
    private static final String DEF_FAIL_MSG = "操作失败";
    private final Map<String, Object> options = new HashMap<>();
    private boolean succ = true;
    private Integer code = 0;
    private String msg;
    private T content;

    public ResponseWrapper(boolean succ) {
        this();
        this.succ = succ;
    }

    public ResponseWrapper(boolean succ, String msg) {
        this(succ);
        this.msg = msg;
    }

    public ResponseWrapper(boolean succ, String msg, T content) {
        this(succ, msg);
        this.content = content;
    }

    public ResponseWrapper(boolean succ, Integer code, String msg, T content) {
        this(succ, msg, content);
        this.code = code;
    }

    public static <T> ResponseWrapper<T> success(Integer code, String msg, T object) {
        return new ResponseWrapper<>(true, code, msg, object);
    }

    public static <T> ResponseWrapper<T> success(String msg, T object) {
        return new ResponseWrapper<>(true, msg, object);
    }

    public static <T> ResponseWrapper<T> success() {
        return new ResponseWrapper<>(true, DEF_SUCC_MSG, null);
    }

    public static <T> ResponseWrapper<T> success(T object) {
        return new ResponseWrapper<>(true, DEF_SUCC_MSG, object);
    }

    public static <T> ResponseWrapper<T> fail(Integer code, String msg) {
        return new ResponseWrapper<>(false, code, msg, null);
    }

    public static <T> ResponseWrapper<T> fail(String msg) {
        return new ResponseWrapper<>(false, msg, null);
    }

    public static <T> ResponseWrapper<T> fail() {
        return fail(DEF_FAIL_MSG);
    }

    private static String parseException2Str(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public ResponseWrapper addOption(String key, Object value) {
        options.put(key, value);
        return this;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public Integer getCode() {
        return code;
    }

    public ResponseWrapper<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseWrapper<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getContent() {
        return content;
    }

    public ResponseWrapper<T> setContent(T content) {
        this.content = content;
        return this;
    }

    public <T> T getOptionValue(String key) {
        return (T) options.get(key);
    }

    public boolean hasValueInOption(String key) {
        return options.containsKey(key);
    }

}
