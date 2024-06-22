package com.profITsoft.flightsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Airport DTO class to hold airport creation and update details data
 */
@Getter
@Builder
@Jacksonized
public class AirportDto {

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "invalid.airport.code")
    private String airportCode;

    @NotBlank(message = "not.blank.name")
    private String name;

    private String city;

    private String country;

    private String timezone;
}
