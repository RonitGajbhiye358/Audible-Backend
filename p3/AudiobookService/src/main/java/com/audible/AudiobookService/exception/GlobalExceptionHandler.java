package com.audible.AudiobookService.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice // Enables global exception handling across all REST controllers
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@Value("${spring.application.name}")
	private String serviceName; // Injects application name from application.properties

	/**
	 * Utility method to construct the error response body with standard metadata.
	 */
	private Map<String, Object> createErrorBody(HttpStatus status, String message, WebRequest request) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		body.put("service", serviceName);
		body.put("path", request.getDescription(false).replace("uri=", ""));
		return body;
	}

	/**
	 * Handles custom ResourceNotFoundException and returns a 404 Not Found response.
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage(), request);
		System.out.println("ResourceNotFoundException handled globally in audiobook service");
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles IllegalArgumentExceptions and returns a 400 Bad Request response.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
	    Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	    System.out.println("IllegalArgumentException handled globally in audiobook service");
	    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles any uncaught RuntimeExceptions and returns a 500 Internal Server Error response.
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
	    Map<String, Object> body = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
	    System.out.println("RuntimeException handled globally in audiobook service");
	    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Overrides default handling for validation failures (e.g. @Valid annotations).
	 * Collects all validation errors and returns them in a structured response.
	 */
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        // Collect all field-specific validation errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Create the error response body and add validation error details
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, "Validation failed on request body", request);
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
