package com.profITsoft.flightsystem.exception;

/**
 * Exception to be thrown when a resource with the same code already exists.
 */
public class ResourceDuplicateException extends RuntimeException {

    public ResourceDuplicateException(String message) {
        super(message);
    }
}
