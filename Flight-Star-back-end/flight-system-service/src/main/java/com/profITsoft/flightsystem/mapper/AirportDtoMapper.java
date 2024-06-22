package com.profITsoft.flightsystem.mapper;

import com.profITsoft.flightsystem.dto.AirportDetailsDto;
import com.profITsoft.flightsystem.dto.AirportDto;
import com.profITsoft.flightsystem.entity.Airport;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * AirportDtoMapper class to map Airport entity to Airport DTOs and vice versa
 */
@Service
public class AirportDtoMapper implements Function<Airport, AirportDetailsDto> {

    /**
     * Map an airport entity to an airport details DTO
     *
     * @param airport airport entity
     * @return airport DTO
     */
    @Override
    public AirportDetailsDto apply(Airport airport) {
        return AirportDetailsDto.builder()
                .id(airport.getId())
                .airportCode(airport.getAirportCode())
                .name(airport.getName())
                .city(airport.getCity())
                .country(airport.getCountry())
                .timezone(airport.getTimezone())
                .build();
    }

    /**
     * Map an airport DTO to an airport entity
     *
     * @param airportDetailsDto airport DTO
     * @return airport entity
     */
    public Airport toAirport(AirportDto airportDetailsDto) {
        Airport airport = new Airport();
        airport.setAirportCode(airportDetailsDto.getAirportCode());
        airport.setName(airportDetailsDto.getName());
        airport.setCity(airportDetailsDto.getCity());
        airport.setCountry(airportDetailsDto.getCountry());
        airport.setTimezone(airportDetailsDto.getTimezone());
        return airport;
    }

    /**
     * Update an airport entity with the provided details
     *
     * @param airport airport entity to update
     * @param airportDto airport DTO with the updated details
     */
    public void updateAirport(Airport airport, AirportDto airportDto) {
        airport.setAirportCode(airportDto.getAirportCode());
        airport.setName(airportDto.getName());
        airport.setCity(airportDto.getCity());
        airport.setCountry(airportDto.getCountry());
        airport.setTimezone(airportDto.getTimezone());
    }
}
