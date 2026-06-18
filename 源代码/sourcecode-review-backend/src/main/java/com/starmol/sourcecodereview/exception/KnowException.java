package com.starmol.sourcecodereview.exception;

public class KnowException extends ApplicationException {
    public KnowException(String message) {
        super(message);
    }
    public KnowException(String message, Throwable cause) {
        super(message, cause);
    }
}
