package com.general.etl.exception;

public class ProcessStopException extends ETLException {
    public ProcessStopException() {
    }

    public ProcessStopException(String message) {
        super(message);
    }

    public ProcessStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessStopException(Throwable cause) {
        super(cause);
    }
}
