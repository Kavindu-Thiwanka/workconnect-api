package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for authentication failures
 */
public class AuthenticationException extends WorkConnectException {
    
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED, cause);
    }

    public AuthenticationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
    
    // Specific authentication error types
    public static class InvalidCredentialsException extends AuthenticationException {
        public InvalidCredentialsException(String message) {
            super(message, "INVALID_CREDENTIALS");
        }
        
        public InvalidCredentialsException() {
            super("Invalid email or password", "INVALID_CREDENTIALS");
        }
    }
    
    public static class AccountLockedException extends AuthenticationException {
        public AccountLockedException(String message) {
            super(message, "ACCOUNT_LOCKED");
        }
    }
    
    public static class AccountDisabledException extends AuthenticationException {
        public AccountDisabledException(String message) {
            super(message, "ACCOUNT_DISABLED");
        }
    }
    
    public static class TokenExpiredException extends AuthenticationException {
        public TokenExpiredException(String message) {
            super(message, "TOKEN_EXPIRED");
        }
        
        public TokenExpiredException() {
            super("Authentication token has expired", "TOKEN_EXPIRED");
        }
    }
    
    public static class InvalidTokenException extends AuthenticationException {
        public InvalidTokenException(String message) {
            super(message, "INVALID_TOKEN");
        }
        
        public InvalidTokenException() {
            super("Invalid authentication token", "INVALID_TOKEN");
        }
    }
    
    public static class EmailAlreadyExistsException extends AuthenticationException {
        public EmailAlreadyExistsException(String email) {
            super(String.format("Email '%s' is already registered", email), "EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }
    }
}
