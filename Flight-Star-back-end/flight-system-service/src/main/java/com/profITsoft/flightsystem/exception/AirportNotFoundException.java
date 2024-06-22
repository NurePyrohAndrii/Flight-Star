package com.profITsoft.flightsystem.exception;

/**
 * AirportNotFoundException class to handle cases when an airport is not found
 */
public class AirportNotFoundException extends ResourceNotFoundException {

    public AirportNotFoundException(String message) {
        super(message);
    }
}
