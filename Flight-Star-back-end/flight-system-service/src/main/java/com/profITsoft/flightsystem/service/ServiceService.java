package com.profITsoft.flightsystem.service;

import com.profITsoft.flightsystem.dto.ServiceDto;
import com.profITsoft.flightsystem.entity.Service;
import com.profITsoft.flightsystem.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * ServiceService class to manage flight services in the database
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public Set<Service> findServicesByNames(Set<String> services) {
        return serviceRepository.findByNameIn(services);
    }

    public List<ServiceDto> getServices() {
        return serviceRepository.findAll().stream()
                .map(service -> ServiceDto.builder()
                        .name(service.getName())
                        .build())
                .toList();
    }
}
