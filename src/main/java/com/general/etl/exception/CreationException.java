package com.general.etl.exception;

public class CreationException extends ETLException {
    public CreationException() {
    }

    public CreationException(String message) {
        super(message);
    }

    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreationException(Throwable cause) {
        super(cause);
    }
}
