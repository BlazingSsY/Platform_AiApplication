package com.starmol.circuitreview.backend.exception;

import com.starmol.circuitreview.backend.common.AppErrorCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends KnowException {
    private final Integer code;

    public AppException(AppErrorCode.ErrorBody error) {
        super(error.getMsg());
        this.code = error.getCode();
    }

    public AppException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
