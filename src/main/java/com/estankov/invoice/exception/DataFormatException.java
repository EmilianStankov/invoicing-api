package com.estankov.invoice.exception;

/**
 * Thrown when the data for an invoice entry is not in the expected format
 */
public class DataFormatException extends RuntimeException {

    public DataFormatException(String message) {
        super(message);
    }

    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
