package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for job application business rule violations
 */
public class JobApplicationException extends WorkConnectException {
    
    public JobApplicationException(String message) {
        super(message, "JOB_APPLICATION_ERROR", HttpStatus.BAD_REQUEST);
    }
    
    public JobApplicationException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    
    public JobApplicationException(String message, Throwable cause) {
        super(message, "JOB_APPLICATION_ERROR", HttpStatus.BAD_REQUEST, cause);
    }

    public JobApplicationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
    
    // Specific job application error types
    public static class DuplicateApplicationException extends JobApplicationException {
        public DuplicateApplicationException(String message) {
            super(message, "DUPLICATE_APPLICATION");
        }
    }
    
    public static class JobNotAvailableException extends JobApplicationException {
        public JobNotAvailableException(String message) {
            super(message, "JOB_NOT_AVAILABLE");
        }
    }
    
    public static class ApplicationDeadlinePassedException extends JobApplicationException {
        public ApplicationDeadlinePassedException(String message) {
            super(message, "APPLICATION_DEADLINE_PASSED");
        }
    }
    
    public static class InvalidApplicationStatusException extends JobApplicationException {
        public InvalidApplicationStatusException(String message) {
            super(message, "INVALID_APPLICATION_STATUS");
        }
    }
    
    public static class UnauthorizedApplicationAccessException extends JobApplicationException {
        public UnauthorizedApplicationAccessException(String message) {
            super(message, "UNAUTHORIZED_APPLICATION_ACCESS", HttpStatus.FORBIDDEN);
        }
        
        public UnauthorizedApplicationAccessException() {
            super("You are not authorized to access this application", "UNAUTHORIZED_APPLICATION_ACCESS", HttpStatus.FORBIDDEN);
        }
    }
}
