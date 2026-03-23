package org.utils.exception;

public class ServiceRequestError extends RuntimeException {
    public ServiceRequestError(String message) {
        super(message);
    }
}
