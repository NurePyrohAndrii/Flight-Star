package com.profITsoft.flightsystem.exception;

/**
 * ResourceNotFoundException class to handle cases when a resource is not found
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
