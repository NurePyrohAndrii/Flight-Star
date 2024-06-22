package com.profITsoft.flightsystem.mapper;

import com.profITsoft.flightsystem.dto.FlightDto;
import com.profITsoft.flightsystem.dto.ListFlightDto;
import com.profITsoft.flightsystem.entity.Airport;
import com.profITsoft.flightsystem.entity.Flight;
import com.profITsoft.flightsystem.entity.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Flight save DTO mapper class to map Flight save DTO to Flight entity
 */
@org.springframework.stereotype.Service
public class FlightDtoMapper {

    /**
     * Map FlightDto to Flight entity
     *
     * @param flightDto flight DTO
     * @param airports airports
     * @param services services
     * @return flight entity
     */
    public Flight toFlight(FlightDto flightDto, Set<Airport> airports, Set<Service> services) {
        Flight flight = new Flight();
        setFlightDetails(flight, flightDto, airports, services);
        return flight;
    }

    /**
     * Update the flight with the provided details
     *
     * @param flight flight entity
     * @param flightDto flight DTO
     * @param airports airports
     * @param services services
     */
    public void updateFlight(Flight flight, FlightDto flightDto, Set<Airport> airports, Set<Service> services) {
        setFlightDetails(flight, flightDto, airports, services);
    }

    /**
     * Map Flight entity to FlightDto
     *
     * @param flight flight entity
     * @return flight DTO
     */
    public ListFlightDto toListFlightDto(Flight flight) {
        return ListFlightDto.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .departureAirport(flight.getDepartureAirport().getAirportCode())
                .arrivalAirport(flight.getArrivalAirport().getAirportCode())
                .departureTime(flight.getDepartureTime().toString())
                .arrivalTime(flight.getArrivalTime().toString())
                .services(flight.getServices().stream().map(Service::getName).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Method to set the flight details to the given flight entity
     *
     * @param flight flight entity
     * @param flightDto flight DTO
     * @param airports airports
     * @param services services
     */
    private void setFlightDetails(Flight flight, FlightDto flightDto, Set<Airport> airports, Set<Service> services) {
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setDepartureAirport(airports.stream().findFirst().get());
        flight.setArrivalAirport(airports.stream().skip(1).findFirst().get());
        flight.setDepartureTime(Instant.parse(flightDto.getDepartureTime()));
        flight.setArrivalTime(Instant.parse(flightDto.getArrivalTime()));
        flight.setServices(services);
    }
}
