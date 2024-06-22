package com.profITsoft.flightsystem.dto;

import com.profITsoft.flightsystem.entity.Service;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

/**
 * Flight details DTO class to hold flight details data
 */
@Getter
@Builder
@Jacksonized
public class FlightDetailsDto {

    private Long id;

    private String flightNumber;

    private AirportDetailsDto departureAirport;

    private AirportDetailsDto arrivalAirport;

    private String departureTime;

    private String arrivalTime;

    private Set<Service> services;
}
