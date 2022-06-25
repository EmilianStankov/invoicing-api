package com.estankov.invoice.exception;

public class DataFormatException extends RuntimeException {

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
