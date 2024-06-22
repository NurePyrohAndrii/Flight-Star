package com.profITsoft.flightsystem.controller;

import com.profITsoft.flightsystem.common.response.ApiResponse;
import com.profITsoft.flightsystem.dto.AirportDetailsDto;
import com.profITsoft.flightsystem.dto.AirportDto;
import com.profITsoft.flightsystem.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Airport controller class to manage airports API
 */
@RestController
@RequestMapping("api/airports")
@RequiredArgsConstructor
public class AirportController {

    private final AirportService airportService;

    /**
     * Get all airports
     *
     * @return list of airports
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AirportDetailsDto>>> getAirports() {
        return ResponseEntity.ok(ApiResponse.success(airportService.getAirports(), HttpStatus.OK.value()));
    }

    /**
     * Save a new airport with the provided details.
     *
     * @param airportDto airport DTO to save
     * @return airport DTO
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AirportDetailsDto>> saveAirport(
            @RequestBody @Valid AirportDto airportDto
    ) {
        AirportDetailsDto createdAirport = airportService.saveAirport(airportDto);
        return ResponseEntity.created(URI.create("api/airports/" + createdAirport.getId()))
                .body(ApiResponse.success(createdAirport, HttpStatus.CREATED.value()));
    }

    /**
     * Update an existing airport with the provided details.
     *
     * @param id airport ID
     * @param airportDto airport DTO to update
     * @return updated airport DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AirportDetailsDto>> updateAirport(
            @PathVariable Long id,
            @RequestBody @Valid AirportDto airportDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(airportService.updateAirport(id, airportDto), HttpStatus.OK.value()));
    }

    /**
     * Delete an existing airport by its ID
     *
     * @param id airport ID
     * @return response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        return ResponseEntity.ok(ApiResponse.success(null, HttpStatus.OK.value()));
    }
}
