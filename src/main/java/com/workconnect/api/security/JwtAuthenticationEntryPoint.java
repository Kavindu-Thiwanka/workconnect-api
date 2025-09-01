package com.workconnect.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.workconnect.api.dto.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Custom authentication entry point for handling JWT authentication failures
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        
        log.warn("Authentication failed [TraceId: {}]: {} for request {}", 
                traceId, authException.getMessage(), request.getRequestURI());

        // Determine the specific error based on the exception or request attributes
        String errorCode = "AUTHENTICATION_FAILED";
        String message = "Authentication failed";
        String details = "Please provide valid authentication credentials";

        // Check if this is a JWT-specific error
        Object jwtError = request.getAttribute("jwt_error");
        if (jwtError != null) {
            switch (jwtError.toString()) {
                case "TOKEN_EXPIRED":
                    errorCode = "TOKEN_EXPIRED";
                    message = "Authentication token has expired";
                    details = "Please login again to get a new token";
                    break;
                case "INVALID_TOKEN":
                    errorCode = "INVALID_TOKEN";
                    message = "Invalid authentication token";
                    details = "Please provide a valid authentication token";
                    break;
                case "TOKEN_MISSING":
                    errorCode = "TOKEN_MISSING";
                    message = "Authentication token is missing";
                    details = "Please provide an authentication token in the Authorization header";
                    break;
                default:
                    // Use default values
                    break;
            }
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
