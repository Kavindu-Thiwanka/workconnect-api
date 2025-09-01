package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown for payment processing errors
 */
public class PaymentException extends WorkConnectException {
    
    public PaymentException(String message) {
        super(message, "PAYMENT_ERROR", HttpStatus.BAD_REQUEST);
    }
    
    public PaymentException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, "PAYMENT_ERROR", HttpStatus.BAD_REQUEST, cause);
    }
    
    public PaymentException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, HttpStatus.BAD_REQUEST, cause);
    }
    
    // Specific payment error types
    public static class PaymentProcessingException extends PaymentException {
        public PaymentProcessingException(String message) {
            super(message, "PAYMENT_PROCESSING_FAILED");
        }
        
        public PaymentProcessingException(String message, Throwable cause) {
            super(message, "PAYMENT_PROCESSING_FAILED", cause);
        }
    }
    
    public static class InsufficientFundsException extends PaymentException {
        public InsufficientFundsException(String message) {
            super(message, "INSUFFICIENT_FUNDS");
        }
    }
    
    public static class PaymentMethodNotFoundException extends PaymentException {
        public PaymentMethodNotFoundException(String message) {
            super(message, "PAYMENT_METHOD_NOT_FOUND");
        }
    }
    
    public static class InvalidPaymentAmountException extends PaymentException {
        public InvalidPaymentAmountException(String message) {
            super(message, "INVALID_PAYMENT_AMOUNT");
        }
    }
    
    public static class PaymentAlreadyProcessedException extends PaymentException {
        public PaymentAlreadyProcessedException(String message) {
            super(message, "PAYMENT_ALREADY_PROCESSED");
        }
    }
}
