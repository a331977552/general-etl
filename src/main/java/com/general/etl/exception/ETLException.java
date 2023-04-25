package com.general.etl.exception;

public class ETLException extends RuntimeException{
    public ETLException() {
    }

    public ETLException(String message) {
        super(message);
    }

    public ETLException(String message, Throwable cause) {
        super(message, cause);
    }

    public ETLException(Throwable cause) {
        super(cause);
    }
}
