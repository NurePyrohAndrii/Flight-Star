package com.profITsoft.flightsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * Report request DTO class to hold report request data
 */
@Getter
@Builder
public class ReportRequest {

    @JsonProperty("departureAirport")
    private String departureAirport;

    @JsonProperty("arrivalAirport")
    private String arrivalAirport;

    @JsonProperty("services")
    private Set<String> services;
}
