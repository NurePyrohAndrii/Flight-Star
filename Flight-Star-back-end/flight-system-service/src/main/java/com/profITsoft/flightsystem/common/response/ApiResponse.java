package com.profITsoft.flightsystem.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * ApiResponse class is a generic class used to return the response of the API.
 * @param <T> The type of the data that is returned by the API.
 */
@Getter
@ToString
public class ApiResponse<T> {
    /**
     * The status of the API response. It can be either "success" or "error".
     */
    private final String status;

    /**
     * The status code of the API response.
     */
    private final Integer statusCode;

    /**
     * The list of errors that occurred during the API call.
     */
    private final List<ApiError> errors;

    /**
     * The data that is returned by the API.
     */
    private final T data;

    /**
     * Constructor for the ApiResponse class.
     * @param status The status of the API response.
     * @param statusCode The status code of the API response.
     * @param errors The list of errors that occurred during the API call.
     * @param data The data that is returned by the API.
     */
    private ApiResponse(@JsonProperty("status") String status,
                        @JsonProperty("statusCode") int statusCode,
                        @JsonProperty("errors") List<ApiError> errors,
                        @JsonProperty("data") T data) {
        this.status = status;
        this.statusCode = statusCode;
        this.errors = errors;
        this.data = data;
    }

    /**
     * Static method to create a success response.
     * @param data The data that is returned by the API.
     * @param <T> The type of the data that is returned by the API.
     * @return The ApiResponse object.
     */
    public static <T> ApiResponse<T> success(T data, int statusCode) {
        return new ApiResponse<>("success", statusCode, null, data);
    }

    /**
     * Static method to create an error response.
     * @param errors The list of errors that occurred during the API call.
     * @return The ApiResponse object.
     */
    public static ApiResponse<List<ApiError>> error(List<ApiError> errors, int statusCode) {
        return new ApiResponse<>("error", statusCode, errors, null);
    }
}
