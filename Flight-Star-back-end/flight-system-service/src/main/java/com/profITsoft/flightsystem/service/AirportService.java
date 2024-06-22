package com.profITsoft.flightsystem.service;

import com.profITsoft.flightsystem.dto.AirportDetailsDto;
import com.profITsoft.flightsystem.dto.AirportDto;
import com.profITsoft.flightsystem.entity.Airport;
import com.profITsoft.flightsystem.exception.AirportDuplicateException;
import com.profITsoft.flightsystem.exception.AirportNotFoundException;
import com.profITsoft.flightsystem.mapper.AirportDtoMapper;
import com.profITsoft.flightsystem.repository.AirportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Airport service class to manage airports
 */
@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportDtoMapper airportDtoMapper;

    /**
     * Get all airports
     *
     * @return list of airports
     */
    public List<AirportDetailsDto> getAirports() {
        return airportRepository.findAll().stream()
                .map(airportDtoMapper).collect(Collectors.toList());
    }

    /**
     * Save a new airport with the provided details.
     *
     * @param airportDto airport DTO to save
     * @return airport DTO
     */
    @Transactional
    public AirportDetailsDto saveAirport(AirportDto airportDto) {
        // Check if an airport with the provided code already exists
        if (airportRepository.existsByAirportCode(airportDto.getAirportCode())) {
            throw new AirportDuplicateException("duplicate.airport.error");
        }

        Airport createdAirport = airportDtoMapper.toAirport(airportDto);
        return airportDtoMapper.apply(airportRepository.save(createdAirport));
    }

    @Transactional
    public AirportDetailsDto updateAirport(Long id, AirportDto airportDto) {
        // Check if an airport with the provided code already exists and is different from the current airport
        Optional<Airport> airportByCode = airportRepository.findByAirportCode(airportDto.getAirportCode());

        if (airportByCode.isPresent() && !airportByCode.get().getId().equals(id)) {
            throw new AirportDuplicateException("duplicate.airport.error");
        }

        // Find the airport by its ID
        Airport airport = airportRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new AirportNotFoundException("not.found.airport"));

        // Update the airport with the provided details and save it
        airportDtoMapper.updateAirport(airport, airportDto);
        return airportDtoMapper.apply(airportRepository.save(airport));
    }

    /**
     * Delete an existing airport by its ID
     *
     * @param id airport ID
     */
    public void deleteAirport(Long id) {
        // Find the airport by its ID
        Airport airport = airportRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new AirportNotFoundException("not.found.airport"));

        // Delete the airport
        airportRepository.delete(airport);
    }

    /**
     * Find airports by their codes (IATA codes)
     *
     * @param departureCode departure airport code
     * @param arrivalCode   arrival airport code
     * @return set of airports
     */
    public Set<Airport> findAirportsByCodes(String departureCode, String arrivalCode) {
        Set<String> airportCodes = new HashSet<>();
        airportCodes.add(departureCode);
        airportCodes.add(arrivalCode);
        return airportRepository.findByAirportCodeIn(airportCodes);
    }
}
