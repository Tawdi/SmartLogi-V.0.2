package com.smartlogi.smartlogidms.common.exception;

import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFound_ShouldReturn404WithCorrectResponse() {
        // Given
        String requestPath = "/api/products/123";
        String errorMessage = "Product not found with id: 123";
        ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

        when(request.getRequestURI()).thenReturn(requestPath);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleResourceNotFound(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found: " + errorMessage, response.getBody().getMessage());
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleValidationExceptions_ShouldReturn400WithFieldErrors() {
        // Given
        String requestPath = "/api/products";

        // Create real FieldError objects
        FieldError fieldError1 = new FieldError("product", "name", "Name is required");
        FieldError fieldError2 = new FieldError("product", "price", "Price must be positive");

        // Create a mock BindingResult
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // Create the exception with the binding result
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        when(request.getRequestURI()).thenReturn(requestPath);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleValidationExceptions(ex, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());

        // Verify errors map is populated
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors() instanceof java.util.Map);
    }

    @Test
    void handleTypeMismatch_ShouldReturn400WithTypeInfo() {
        // Given
        String requestPath = "/api/products/abc";
        String paramName = "productId";

        // Create a real MethodArgumentTypeMismatchException
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, paramName, null, null);

        when(request.getRequestURI()).thenReturn(requestPath);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleTypeMismatch(ex, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Parameter '" + paramName + "'"));
        assertTrue(response.getBody().getMessage().contains("Long"));
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleMethodNotSupported_ShouldReturn405WithSupportedMethods() {
        // Given
        String requestPath = "/api/products";
        String method = "PATCH";

        // Create real exception with supported methods
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException(
                "PATCH",
                Arrays.asList("GET", "POST", "PUT", "DELETE")
        );

        when(request.getRequestURI()).thenReturn(requestPath);
        when(request.getMethod()).thenReturn(method);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleMethodNotSupported(ex, request);

        // Then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("HTTP method 'PATCH' is not supported"));
        assertTrue(response.getBody().getMessage().contains("Supported:"));
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleJsonError_ShouldReturn400ForInvalidJSON() {
        // Given
        // Use a real exception
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON");

        // When
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleJsonError(ex);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid JSON ", response.getBody().getMessage());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleIllegalState_ShouldReturn400ForBusinessRuleViolation() {
        // Given
        String errorMessage = "Order cannot be cancelled in current status";
        IllegalStateException ex = new IllegalStateException(errorMessage);

        // When
        ResponseEntity<ApiResponseDTO<Void>> response =
                exceptionHandler.handleIllegalState(ex, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals("ERROR", response.getBody().getStatus());

    }

    @Test
    void handleNoResourceFound_ShouldReturn404ForWrongURL() {
        // Given
        String requestPath = "/api/nonexistent";
        String method = "GET";

        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET,requestPath);

        when(request.getRequestURI()).thenReturn(requestPath);
        when(request.getMethod()).thenReturn(method);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleNoResourceFound(ex, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("The requested endpoint"));
        assertTrue(response.getBody().getMessage().contains(requestPath));
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleGenericException_ShouldReturn500ForUnexpectedErrors() {
        // Given
        String requestPath = "/api/products";
        String errorMessage = "Database connection failed";
        Exception ex = new RuntimeException(errorMessage);

        when(request.getRequestURI()).thenReturn(requestPath);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleGenericException(ex, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred. Please contact support.", response.getBody().getMessage());
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleGenericException_WithNullRequestPath_ShouldHandleGracefully() {
        // Given
        Exception ex = new RuntimeException("Test exception");

        when(request.getRequestURI()).thenReturn(null);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleGenericException(ex, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred. Please contact support.", response.getBody().getMessage());
        assertNull(response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }

    @Test
    void handleValidationExceptions_WithEmptyErrors_ShouldHandleGracefully() {
        // Given
        String requestPath = "/api/products";

        // Create mock BindingResult with empty errors
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList());

        // Create the exception
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        when(request.getRequestURI()).thenReturn(requestPath);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleValidationExceptions(ex, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(requestPath, response.getBody().getPath());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors() instanceof java.util.Map);
    }

    @Test
    void handleMethodNotSupported_WithNullSupportedMethods_ShouldHandleGracefully() {
        // Given
        String requestPath = "/api/products";
        String method = "PATCH";

        // Create exception without supported methods
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("PATCH");

        when(request.getRequestURI()).thenReturn(requestPath);
        when(request.getMethod()).thenReturn(method);

        // When
        ResponseEntity<ApiResponseDTO<String>> response =
                exceptionHandler.handleMethodNotSupported(ex, request);

        // Then
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("HTTP method 'PATCH' is not supported"));
        assertEquals(requestPath, response.getBody().getPath());
        assertEquals("ERROR", response.getBody().getStatus());
    }
}