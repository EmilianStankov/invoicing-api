package com.estankov.invoice.exception;

/**
 * Thrown when an invoice contains data inconsistencies like a missing parent document, unsupported currency, etc.
 */
public class InconsistentDataException extends RuntimeException {

    public InconsistentDataException(String message) {
        super(message);
    }

    public InconsistentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
