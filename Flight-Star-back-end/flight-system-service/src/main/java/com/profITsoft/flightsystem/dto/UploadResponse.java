package com.profITsoft.flightsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * Upload response DTO class to hold upload response data, succeeded and failed count of records uploaded
 */
@Getter
@Builder
@Jacksonized
public class UploadResponse {

    private String succeeded;

    private String failed;
}
