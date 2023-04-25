package com.general.etl.exception;

public class ProcessStartException extends ETLException {
    public ProcessStartException() {
    }

    public ProcessStartException(String message) {
        super(message);
    }

    public ProcessStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessStartException(Throwable cause) {
        super(cause);
    }
}
