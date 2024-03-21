package com.example.todolist.controller;

import com.example.todolist.exceptions.ToDoNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for handling validation errors and ToDoNotFoundException.
 */
@RestControllerAdvice
public class ValidationExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException thrown during request body validation.
     * @param ex The MethodArgumentNotValidException to handle.
     * @return ResponseEntity containing error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = extractFieldErrors(ex);
        Map<String, Object> responseBody = createResponseBody(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    /**
     * Handles ToDoNotFoundException thrown when a _ToDo_ entity is not found.
     * @param ex The ToDoNotFoundException to handle.
     * @return ResponseEntity containing error response.
     */
    @ExceptionHandler(ToDoNotFoundException.class)
    public ResponseEntity<Object> handleToDoNotFoundException(ToDoNotFoundException ex) {
        Map<String, Object> responseBody = createResponseBody(ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    /**
     * Extracts field errors from MethodArgumentNotValidException.
     * @param ex The MethodArgumentNotValidException to extract errors from.
     * @return Map containing field errors.
     */
    private Map<String, String> extractFieldErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Creates a response body for error responses.
     * @param errors The validation errors or ToDoNotFoundException message.
     * @return Map containing error response body.
     */
    private Map<String, Object> createResponseBody(Object errors) {
        Map<String, Object> responseBody = new HashMap<>();
        fillResponseBody(responseBody, errors instanceof Map ? HttpStatus.BAD_REQUEST.value() : HttpStatus.NOT_FOUND.value());
        responseBody.put("errors", errors);
        return responseBody;
    }

    /**
     * Fills response body with common fields.
     * @param responseBody The response body to fill.
     * @param httpStatus The HTTP status code.
     */
    private void fillResponseBody(Map<String, Object> responseBody, int httpStatus){
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", httpStatus);
        responseBody.put("path", getPath());
    }

    /**
     * Retrieves the request path.
     * @return The request path.
     */
    private String getPath() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getRequestURI();
    }
}
