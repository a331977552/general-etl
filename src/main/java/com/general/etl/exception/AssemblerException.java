package com.general.etl.exception;

public class AssemblerException extends ETLException {
    public AssemblerException() {
    }

    public AssemblerException(String message) {
        super(message);
    }

    public AssemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssemblerException(Throwable cause) {
        super(cause);
    }
}
