package com.starmol.circuitreview.backend.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenExpiredException extends AppException {
    public TokenExpiredException(Integer code, String message) {
        super(code, message);
    }
}
