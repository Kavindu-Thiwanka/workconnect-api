package com.workconnect.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when there are issues with job status operations.
 */
public class JobStatusException extends WorkConnectException {

    public JobStatusException(String message) {
        super(message, "JOB_STATUS_ERROR", HttpStatus.BAD_REQUEST);
    }

    public JobStatusException(String message, Throwable cause) {
        super(message, "JOB_STATUS_ERROR", HttpStatus.BAD_REQUEST, cause);
    }
}
