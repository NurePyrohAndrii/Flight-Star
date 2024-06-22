package com.profITsoft.flightsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Service DTO class to hold service details data
 */
@Getter
@Builder
@Jacksonized
public class ServiceDto {

    /**
     * Name of the service
     */
    @NotBlank(message="not.blank.service.name")
    private String name;
}
