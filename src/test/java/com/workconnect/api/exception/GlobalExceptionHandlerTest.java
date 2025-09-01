package com.workconnect.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workconnect.api.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    private final String testPath = "/api/test";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(testPath);
    }

    @Test
    void shouldHandleWorkConnectException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("User", 1L);

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleWorkConnectException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo("User with id '1' not found");
        assertThat(response.getBody().getPath()).isEqualTo(testPath);
        assertThat(response.getBody().getTraceId()).isNotNull();
    }

    @Test
    void shouldHandleValidationException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("user", "email", "Email is required");
        FieldError fieldError2 = new FieldError("user", "password", "Password is too short");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleValidationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed for one or more fields");
        assertThat(response.getBody().getFieldErrors()).hasSize(2);
        assertThat(response.getBody().getFieldErrors()).containsEntry("email", "Email is required");
        assertThat(response.getBody().getFieldErrors()).containsEntry("password", "Password is too short");
    }

    @Test
    void shouldHandleAuthenticationException() {
        // Given
        AuthenticationException exception = new AuthenticationException("Bad credentials") {};

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleAuthenticationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getErrorCode()).isEqualTo("AUTHENTICATION_FAILED");
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication failed");
        assertThat(response.getBody().getDetails()).isEqualTo("Please check your credentials and try again");
    }

    @Test
    void shouldHandleAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleAccessDeniedException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getErrorCode()).isEqualTo("ACCESS_DENIED");
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    @Test
    void shouldHandleDataIntegrityViolationException() {
        // Given
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "unique constraint violation");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleDataIntegrityViolationException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getErrorCode()).isEqualTo("DUPLICATE_ENTRY");
        assertThat(response.getBody().getMessage()).isEqualTo("A record with this information already exists");
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleIllegalArgumentException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_ARGUMENT");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument");
    }

    @Test
    void shouldHandleRuntimeException() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleRuntimeException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getDetails()).isEqualTo("Please try again later or contact support if the problem persists");
    }

    @Test
    void shouldHandlePaymentException() {
        // Given
        PaymentException.InvalidPaymentAmountException exception = 
                new PaymentException.InvalidPaymentAmountException("Amount must be positive");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleWorkConnectException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_PAYMENT_AMOUNT");
        assertThat(response.getBody().getMessage()).isEqualTo("Amount must be positive");
    }

    @Test
    void shouldHandleJobApplicationException() {
        // Given
        JobApplicationException.DuplicateApplicationException exception = 
                new JobApplicationException.DuplicateApplicationException("Already applied");

        // When
        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler
                .handleWorkConnectException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrorCode()).isEqualTo("DUPLICATE_APPLICATION");
        assertThat(response.getBody().getMessage()).isEqualTo("Already applied");
    }
}
