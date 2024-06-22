package com.profITsoft.flightsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Getter
@Builder
@Jacksonized
public class ListFlightDto {

    private Long id;

    private String flightNumber;

    private String departureAirport;

    private String arrivalAirport;

    private String departureTime;

    private String arrivalTime;

    private Set<String> services;

}
