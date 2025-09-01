package com.workconnect.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response format for API errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String error;
    private String message;
    private String path;
    private int status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public static ErrorResponse of(String error, String message, String path, int status) {
        return ErrorResponse.builder()
                .error(error)
                .message(message)
                .path(path)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
