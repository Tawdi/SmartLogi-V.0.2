package com.smartlogi.smartlogidms.common.exception;

import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler
 * – returns {@link  ApiResponseDTO} for every error.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* --------------------------------------------------------------------- *
     *  1. ResourceNotFoundException (your custom 404)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        logger.error("== RESOURCE NOT FOUND ==> {}", ex.getMessage());

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "Resource not found: " + ex.getMessage());
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /* --------------------------------------------------------------------- *
     *  2. Validation errors (@Valid, @Validated)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDTO<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String msg   = error.getDefaultMessage();
            errors.put(field, msg);
        });

        logger.warn(" == VALIDATION FAILED ==>  {}", errors);

        ApiResponseDTO<String> response = ApiResponseDTO.error("Validation failed", errors);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  3. Path-variable
     * --------------------------------------------------------------------- */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        logger.error(" Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        String msg = String.format(
                "Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ApiResponseDTO<String> response = ApiResponseDTO.error(msg);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* --------------------------------------------------------------------- *
     *  4. HTTP 405 – Method Not Allowed (PATCH, DELETE, etc. not supported)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        logger.warn("HTTP method not supported: {} {}", request.getMethod(), request.getRequestURI());

        String supported = ex.getSupportedHttpMethods() != null
                ? ex.getSupportedHttpMethods().toString()
                : "none";

        String message = String.format(
                "HTTP method '%s' is not supported for this endpoint. Supported: %s",
                request.getMethod(), supported);

        ApiResponseDTO<String> response = ApiResponseDTO.error(message);
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /* --------------------------------------------------------------------- *
     *  5. Catch-all for any other exception (500)
     * --------------------------------------------------------------------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error", ex);

        ApiResponseDTO<String> response = ApiResponseDTO.error(
                "An unexpected error occurred. Please contact support.");
        response.setPath(request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}