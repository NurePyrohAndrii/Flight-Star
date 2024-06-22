package com.profITsoft.flightsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Airport DTO class to hold airport details data
 */
@Getter
@Builder
@Jacksonized
public class AirportDetailsDto {

    private Long id;

    private String airportCode;

    private String name;

    private String city;

    private String country;

    private String timezone;
}
