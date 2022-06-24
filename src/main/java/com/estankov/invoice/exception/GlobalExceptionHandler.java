package com.estankov.invoice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CsvParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(CsvParseException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .path(request.getRequestURI())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(BindException exception, HttpServletRequest request) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(exception.getMessage())
                        .path(request.getRequestURI())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
