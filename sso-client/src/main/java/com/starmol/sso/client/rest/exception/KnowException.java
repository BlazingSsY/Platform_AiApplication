package com.starmol.sso.client.rest.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 业务异常类
 *
 * @author guojingyi
 * @date 2022-04-12 16:25
 */
@Getter
@Setter
@Accessors(chain = true)
public class KnowException extends RuntimeException {

    Integer code;

    Object content;

    public KnowException(Exception e) {
        super(e);
    }

    public KnowException(Exception e, Integer code) {
        super(e);
        this.code = code;
    }

    public KnowException(String message) {
        super(message);
    }

    public KnowException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public KnowException(String message, Exception e) {
        super(message, e);
    }

    public KnowException(String message, Exception e, Integer code) {
        super(message, e);
        this.code = code;
    }

    public KnowException(String message, Integer code, Object content) {
        super(message);
        this.code = code;
        this.content = content;
    }
}
