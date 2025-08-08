package com.workconnect.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for WorkConnect application
 */
@Getter
public abstract class WorkConnectException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    protected WorkConnectException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    protected WorkConnectException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
