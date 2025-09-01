package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when business rules are violated
 */
public class BusinessRuleException extends WorkConnectException {
    
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST);
    }
    
    public BusinessRuleException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    
    public BusinessRuleException(String message, Throwable cause) {
        super(message, "BUSINESS_RULE_VIOLATION", HttpStatus.BAD_REQUEST, cause);
    }
    
    public BusinessRuleException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HttpStatus.BAD_REQUEST, cause);
    }
}
