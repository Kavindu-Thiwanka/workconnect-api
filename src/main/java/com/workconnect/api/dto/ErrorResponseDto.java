package com.workconnect.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response DTO for consistent API error handling
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * Error code for frontend handling (e.g., "VALIDATION_ERROR", "RESOURCE_NOT_FOUND")
     */
    private String errorCode;
    
    /**
     * User-friendly error message
     */
    private String message;
    
    /**
     * Detailed error description (optional, for debugging)
     */
    private String details;
    
    /**
     * Request path where the error occurred
     */
    private String path;
    
    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    /**
     * Validation errors for field-specific issues
     */
    private Map<String, String> fieldErrors;
    
    /**
     * List of validation error messages
     */
    private List<String> validationErrors;
    
    /**
     * Trace ID for request tracking (useful for debugging)
     */
    private String traceId;
    
    /**
     * Create a simple error response
     */
    public static ErrorResponseDto of(int status, String errorCode, String message, String path) {
        return ErrorResponseDto.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create an error response with details
     */
    public static ErrorResponseDto of(int status, String errorCode, String message, String details, String path) {
        return ErrorResponseDto.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a validation error response
     */
    public static ErrorResponseDto validationError(String path, Map<String, String> fieldErrors) {
        return ErrorResponseDto.builder()
                .status(400)
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed for one or more fields")
                .path(path)
                .fieldErrors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Create a validation error response with list of errors
     */
    public static ErrorResponseDto validationError(String path, List<String> validationErrors) {
        return ErrorResponseDto.builder()
                .status(400)
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .path(path)
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
