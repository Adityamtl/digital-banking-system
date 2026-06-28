package com.spring_project.digital_banking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 *
 * <p>Catches exceptions thrown during request processing and returns
 * consistent JSON error responses with appropriate HTTP status codes.
 * Uses smart detection to differentiate between "not found" and "bad request" errors.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", ex.getMessage());
        
        // Simple logic to map exception types to status codes
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        }
        
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }
}