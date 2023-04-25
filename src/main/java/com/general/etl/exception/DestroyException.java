package com.general.etl.exception;

public class DestroyException extends ETLException {
    public DestroyException() {
    }

    public DestroyException(String message) {
        super(message);
    }

    public DestroyException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestroyException(Throwable cause) {
        super(cause);
    }
}
