package com.general.etl.exception;

public class LifeCycleException extends ETLException {
    public LifeCycleException() {
    }

    public LifeCycleException(String message) {
        super(message);
    }

    public LifeCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LifeCycleException(Throwable cause) {
        super(cause);
    }
}
