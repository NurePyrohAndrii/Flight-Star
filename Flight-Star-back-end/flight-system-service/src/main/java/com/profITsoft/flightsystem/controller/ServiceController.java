package com.profITsoft.flightsystem.controller;

import com.profITsoft.flightsystem.common.response.ApiResponse;
import com.profITsoft.flightsystem.dto.ServiceDto;
import com.profITsoft.flightsystem.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Service controller class to manage services API
 */
@RestController
@RequestMapping("api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * Get all services
     *
     * @return list of services
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getServices() {
        return ResponseEntity.ok(ApiResponse.success(serviceService.getServices(), HttpStatus.OK.value()));
    }

}
