package com.profITsoft.flightsystem.controller;

import com.profITsoft.flightsystem.common.response.ApiResponse;
import com.profITsoft.flightsystem.common.util.PaginationHeadersUtils;
import com.profITsoft.flightsystem.dto.*;
import com.profITsoft.flightsystem.service.FlightService;
import com.profITsoft.flightsystem.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Flight controller class to manage flights API
 */
@RestController
@RequestMapping("api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final PaginationHeadersUtils paginationHeadersUtils;
    private final ReportService reportService;

    /**
     * Save a flight with the provided details in flightSaveDto.
     *
     * @param flightDto flight save DTO
     * @return flight details DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FlightDetailsDto>> saveFlight(
            @RequestBody @Valid FlightDto flightDto
    ) {
        FlightDetailsDto createdFlight = flightService.saveFlight(flightDto);
        return ResponseEntity.created(URI.create("api/flights/" + createdFlight.getId()))
                .body(ApiResponse.success(createdFlight, HttpStatus.CREATED.value()));
    }

    /**
     * Get flight details by flightId.
     *
     * @param flightId flight ID
     * @return flight details DTO
     */
    @GetMapping("/{flightId}")
    public ResponseEntity<ApiResponse<FlightDetailsDto>> getFlightDetails(
            @PathVariable Long flightId
    ) {
        return ResponseEntity.ok(ApiResponse.success(flightService.getFlightDetails(flightId), HttpStatus.OK.value()));
    }

    /**
     * List flights with pagination and filter options.
     *
     * @param flightListRequest flight list request
     * @return list of flight details DTO
     */
    @PostMapping("/_list")
    public ResponseEntity<ApiResponse<PageResponse<List<ListFlightDto>>>> listFlights(
            @RequestBody @Valid FlightListRequest flightListRequest
    ) {
        Pageable pageable = PageRequest.of(flightListRequest.getPage(), flightListRequest.getSize());
        Page<ListFlightDto> flights = flightService.listFlights(flightListRequest, pageable);
        return ResponseEntity.ok()
                .headers(paginationHeadersUtils.createPaginationHeaders(flights, pageable))
                .body(ApiResponse.success(PageResponse.of(flights), HttpStatus.OK.value()));
    }

    /**
     * Update a flight with the provided details in flightSaveDto.
     *
     * @param flightDto flight save DTO
     * @return flight details DTO
     */
    @PutMapping("/{flightId}")
    public ResponseEntity<ApiResponse<FlightDetailsDto>> updateFlight(
            @PathVariable Long flightId,
            @RequestBody @Valid FlightDto flightDto
    ) {
        FlightDetailsDto updatedFlight = flightService.updateFlight(flightId, flightDto);
        return ResponseEntity.ok(ApiResponse.success(updatedFlight, HttpStatus.OK.value()));
    }

    /**
     * Delete a flight by flightId.
     *
     * @param flightId flight ID
     * @return flight details DTO
     */
    @DeleteMapping("/{flightId}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(
            @PathVariable Long flightId
    ) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.ok(ApiResponse.success(null, HttpStatus.OK.value()));
    }

    /**
     * Generate a report for the list of flights.
     *
     * @param request report request
     * @return CSV file
     */
    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping(value = "/_report")
    public ResponseEntity<byte[]> getFlightReport(
            @RequestBody ReportRequest request
    ) {
        // Create a flight list request object with page and size set to 0, to reuse the listFlights method
        FlightListRequest flightListRequest = FlightListRequest.builder()
                .departureAirport(request.getDepartureAirport())
                .arrivalAirport(request.getArrivalAirport())
                .services(request.getServices())
                .page(0)
                .size(0)
                .build();

        List<ListFlightDto> flights = flightService.listFlights(flightListRequest, Pageable.unpaged()).getContent();
        byte[] csvContent;
        try {
            csvContent = reportService.generateCsvReport(flights);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to generate report".getBytes());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=flights.csv");
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE));

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }

    /**
     * Upload flights from a JSON file.
     *
     * @param file JSON file
     * @return upload response
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadFlights(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(ApiResponse.success(flightService.uploadFlights(file), HttpStatus.OK.value()));
    }
}
