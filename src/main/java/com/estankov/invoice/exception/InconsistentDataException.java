package com.estankov.invoice.exception;

public class InconsistentDataException extends RuntimeException {

    public InconsistentDataException(String message) {
        super(message);
    }

    public InconsistentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
