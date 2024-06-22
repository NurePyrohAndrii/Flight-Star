package com.profITsoft.flightsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * Flight list request DTO class to hold flight list request data
 */
@Getter
@Builder
public class FlightListRequest {

    @JsonProperty("departureAirport")
    private String departureAirport;

    @JsonProperty("arrivalAirport")
    private String arrivalAirport;

    @JsonProperty("services")
    private Set<String> services;

    @JsonProperty("page")
    @Min(value = 0, message = "invalid.page")
    @NotNull(message = "invalid.page")
    private Integer page;

    @JsonProperty("size")
    @Min(value = 1, message = "invalid.page.size")
    @NotNull(message = "invalid.page.size")
    private Integer size;
}
