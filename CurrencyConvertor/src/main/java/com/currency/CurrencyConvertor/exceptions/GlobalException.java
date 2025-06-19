package com.currency.CurrencyConvertor.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.dao.DataAccessException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientError(HttpClientErrorException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "External API error", ex);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Object> handleResourceAccess(ResourceAccessException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "External service unavailable", ex);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClient(RestClientException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Rest client error", ex);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJson(JsonProcessingException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "JSON processing error", ex);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccess(DataAccessException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database error", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Illegal argument", ex);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointer(NullPointerException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected null encountered", ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "General error occurred", ex);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message, Exception ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("details", ex.getMessage());

        return new ResponseEntity<>(errorBody, status);
    }
}
