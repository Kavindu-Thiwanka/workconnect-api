package com.workconnect.api.exception;

import com.workconnect.api.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for centralized error handling across the application
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom WorkConnect exceptions
     */
    @ExceptionHandler(WorkConnectException.class)
    public ResponseEntity<ErrorResponseDto> handleWorkConnectException(
            WorkConnectException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("WorkConnect exception occurred [TraceId: {}]: {}", traceId, ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(ex.getHttpStatus().value())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Validation error occurred [TraceId: {}]: {}", traceId, ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed for one or more fields")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Constraint violation occurred [TraceId: {}]: {}", traceId, ex.getMessage());

        List<String> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("CONSTRAINT_VIOLATION")
                .message("Constraint validation failed")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle Spring Security authentication exceptions
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Authentication exception occurred [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("AUTHENTICATION_FAILED")
                .message("Authentication failed")
                .details("Please check your credentials and try again")
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle Spring Security access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Access denied [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode("ACCESS_DENIED")
                .message("Access denied")
                .details("You don't have permission to access this resource")
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle database constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("Data integrity violation [TraceId: {}]: {}", traceId, ex.getMessage(), ex);

        String message = "Data integrity violation";
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        // Check for common constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("duplicate key")) {
                message = "A record with this information already exists";
                errorCode = "DUPLICATE_ENTRY";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Cannot perform this operation due to related data";
                errorCode = "FOREIGN_KEY_VIOLATION";
            } else if (ex.getMessage().contains("not-null constraint")) {
                message = "Required field cannot be empty";
                errorCode = "NOT_NULL_VIOLATION";
            }
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.value())
                .errorCode(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle HTTP message not readable exceptions (malformed JSON)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("HTTP message not readable [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("MALFORMED_REQUEST")
                .message("Request body is malformed or invalid")
                .details("Please check your request format and try again")
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing request parameter exceptions
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Missing request parameter [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("MISSING_PARAMETER")
                .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle method argument type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Method argument type mismatch [TraceId: {}]: {}", traceId, ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(), ex.getName());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_PARAMETER_TYPE")
                .message(message)
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle HTTP request method not supported exceptions
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("HTTP method not supported [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .errorCode("METHOD_NOT_ALLOWED")
                .message(String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()))
                .details(String.format("Supported methods: %s", String.join(", ", ex.getSupportedMethods())))
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle no handler found exceptions (404 errors)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("No handler found [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode("ENDPOINT_NOT_FOUND")
                .message("The requested endpoint was not found")
                .details(String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()))
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Illegal argument [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.warn("Illegal state [TraceId: {}]: {}", traceId, ex.getMessage());

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.value())
                .errorCode("ILLEGAL_STATE")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handle generic runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("Unexpected runtime exception [TraceId: {}]: {}", traceId, ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .details("Please try again later or contact support if the problem persists")
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, HttpServletRequest request) {

        String traceId = generateTraceId();

        log.error("Unexpected exception [TraceId: {}]: {}", traceId, ex.getMessage(), ex);

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .details("Please try again later or contact support if the problem persists")
                .path(request.getRequestURI())
                .timestamp(java.time.LocalDateTime.now())
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Generate a unique trace ID for request tracking
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
