package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends WorkConnectException {
    
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s with id '%s' not found", resourceType, id), 
              "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, cause);
    }
}
