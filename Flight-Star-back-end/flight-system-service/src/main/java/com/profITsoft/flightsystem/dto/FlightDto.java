package com.profITsoft.flightsystem.dto;

import com.profITsoft.flightsystem.common.validation.ValidDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * Flight save DTO class to save flight details
 */
@Getter
@Builder
@Jacksonized
public class FlightDto {

    /**
     * Number of the flight
     */
    @Pattern(regexp = "^[A-Za-z]{2}\\d{1,4}$", message = "invalid.flight.number")
    private String flightNumber;

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "invalid.airport.code")
    private String departureAirport;

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "invalid.airport.code")
    private String arrivalAirport;

    @ValidDate
    @NotBlank(message = "not.blank.departure.time")
    private String departureTime;

    @ValidDate
    @NotBlank(message = "not.blank.arrival.time")
    private String arrivalTime;

    @NotEmpty(message = "not.empty.services")
    private Set<String> services;

}
