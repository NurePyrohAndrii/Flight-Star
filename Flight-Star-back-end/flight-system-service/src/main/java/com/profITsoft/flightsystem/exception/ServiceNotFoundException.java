package com.profITsoft.flightsystem.exception;

/**
 * ServiceNotFoundException class to handle cases when flight services are not found
 */
public class ServiceNotFoundException extends ResourceNotFoundException {

    public ServiceNotFoundException(String message) {
        super(message);
    }
}
