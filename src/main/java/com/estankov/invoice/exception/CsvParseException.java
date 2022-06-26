package com.estankov.invoice.exception;

/**
 * Thrown when the passed .csv could not be parsed
 */
public class CsvParseException extends RuntimeException {

    public CsvParseException(String message) {
        super(message);
    }

    public CsvParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
