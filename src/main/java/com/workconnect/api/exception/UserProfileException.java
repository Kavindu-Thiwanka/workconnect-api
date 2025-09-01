package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for user profile validation and business rule violations
 */
public class UserProfileException extends WorkConnectException {
    
    public UserProfileException(String message) {
        super(message, "USER_PROFILE_ERROR", HttpStatus.BAD_REQUEST);
    }
    
    public UserProfileException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    
    public UserProfileException(String message, Throwable cause) {
        super(message, "USER_PROFILE_ERROR", HttpStatus.BAD_REQUEST, cause);
    }

    public UserProfileException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    public UserProfileException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HttpStatus.BAD_REQUEST, cause);
    }
    
    // Specific user profile error types
    public static class ProfileIncompleteException extends UserProfileException {
        public ProfileIncompleteException(String message) {
            super(message, "PROFILE_INCOMPLETE");
        }
    }
    
    public static class InvalidProfileDataException extends UserProfileException {
        public InvalidProfileDataException(String message) {
            super(message, "INVALID_PROFILE_DATA");
        }
    }
    
    public static class ProfileImageUploadException extends UserProfileException {
        public ProfileImageUploadException(String message) {
            super(message, "PROFILE_IMAGE_UPLOAD_FAILED");
        }
        
        public ProfileImageUploadException(String message, Throwable cause) {
            super(message, "PROFILE_IMAGE_UPLOAD_FAILED", cause);
        }
    }
    
    public static class UnauthorizedProfileAccessException extends UserProfileException {
        public UnauthorizedProfileAccessException(String message) {
            super(message, "UNAUTHORIZED_PROFILE_ACCESS", HttpStatus.FORBIDDEN);
        }
        
        public UnauthorizedProfileAccessException() {
            super("You are not authorized to access this profile", "UNAUTHORIZED_PROFILE_ACCESS", HttpStatus.FORBIDDEN);
        }
    }
}
