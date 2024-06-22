package com.profITsoft.flightsystem.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;;

/**
 * ApiError class to hold the error details
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiError {
    /**
     * HttpStatus status of the error response
     */
    HttpStatus status;

    /**
     * Error message to be displayed to the user
     */
    String message;

    /**
     * Details of the error occurred in the application
     */
    String details;

    /**
     * Timestamp of the error occurred
     */
    String timestamp;
}
