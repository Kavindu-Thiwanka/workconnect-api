package com.workconnect.api.exception;

/**
 * Constants for error codes used throughout the application
 */
public final class ErrorCodes {
    
    // Validation Errors
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
    public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String MISSING_PARAMETER = "MISSING_PARAMETER";
    public static final String INVALID_PARAMETER_TYPE = "INVALID_PARAMETER_TYPE";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";
    
    // Authentication & Authorization Errors
    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    
    // Resource Errors
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";
    
    // Business Rule Errors
    public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
    public static final String ILLEGAL_STATE = "ILLEGAL_STATE";
    
    // Job Application Errors
    public static final String JOB_APPLICATION_ERROR = "JOB_APPLICATION_ERROR";
    public static final String DUPLICATE_APPLICATION = "DUPLICATE_APPLICATION";
    public static final String JOB_NOT_AVAILABLE = "JOB_NOT_AVAILABLE";
    public static final String APPLICATION_DEADLINE_PASSED = "APPLICATION_DEADLINE_PASSED";
    public static final String INVALID_APPLICATION_STATUS = "INVALID_APPLICATION_STATUS";
    public static final String UNAUTHORIZED_APPLICATION_ACCESS = "UNAUTHORIZED_APPLICATION_ACCESS";
    
    // User Profile Errors
    public static final String USER_PROFILE_ERROR = "USER_PROFILE_ERROR";
    public static final String PROFILE_INCOMPLETE = "PROFILE_INCOMPLETE";
    public static final String INVALID_PROFILE_DATA = "INVALID_PROFILE_DATA";
    public static final String PROFILE_IMAGE_UPLOAD_FAILED = "PROFILE_IMAGE_UPLOAD_FAILED";
    public static final String UNAUTHORIZED_PROFILE_ACCESS = "UNAUTHORIZED_PROFILE_ACCESS";
    
    // Payment Errors
    public static final String PAYMENT_ERROR = "PAYMENT_ERROR";
    public static final String PAYMENT_PROCESSING_FAILED = "PAYMENT_PROCESSING_FAILED";
    public static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
    public static final String PAYMENT_METHOD_NOT_FOUND = "PAYMENT_METHOD_NOT_FOUND";
    public static final String INVALID_PAYMENT_AMOUNT = "INVALID_PAYMENT_AMOUNT";
    public static final String PAYMENT_ALREADY_PROCESSED = "PAYMENT_ALREADY_PROCESSED";
    
    // Database Errors
    public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String DUPLICATE_ENTRY = "DUPLICATE_ENTRY";
    public static final String FOREIGN_KEY_VIOLATION = "FOREIGN_KEY_VIOLATION";
    public static final String NOT_NULL_VIOLATION = "NOT_NULL_VIOLATION";
    
    // HTTP Errors
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    
    // Private constructor to prevent instantiation
    private ErrorCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
