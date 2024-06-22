package com.profITsoft.flightsystem.service;

import com.profITsoft.flightsystem.dto.*;
import com.profITsoft.flightsystem.entity.Airport;
import com.profITsoft.flightsystem.entity.Flight;
import com.profITsoft.flightsystem.entity.Service;
import com.profITsoft.flightsystem.exception.AirportNotFoundException;
import com.profITsoft.flightsystem.exception.FlightNotFoundException;
import com.profITsoft.flightsystem.exception.FlightScheduleException;
import com.profITsoft.flightsystem.exception.ServiceNotFoundException;
import com.profITsoft.flightsystem.mapper.FlightDetailsDtoMapper;
import com.profITsoft.flightsystem.mapper.FlightDtoMapper;
import com.profITsoft.flightsystem.messaging.SendMailMessage;
import com.profITsoft.flightsystem.repository.FlightRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Flight service class to manage flights
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class FlightService {

    @Value("${kafka.topic.mail}")
    private String mailTopic;

    private final FlightRepository flightRepository;
    private final AirportService airportService;
    private final ServiceService serviceService;
    private final FlightDtoMapper flightDtoMapper;
    private final FlightDetailsDtoMapper flightDetailsDtoMapper;
    private final FlightJsonParser flightJsonParser;
    private final KafkaOperations<String, SendMailMessage> kafkaOperations;

    /**
     * Save a new flight with the provided details.
     *
     * @param flightDto flight DTO
     * @return flight details DTO
     */
    @Transactional
    public FlightDetailsDto saveFlight(FlightDto flightDto) {
        // validate the flight request
        FlightRequestValidationResult result = getFlightRequestValidationResult(flightDto);

        // ensure that the combination of flight number, departure and arrival airports,
        // departure and arrival time is unique, so that we don't have two flights
        // with the same number departing at the same time
        if (flightRepository.existsByFlightDetails(
                flightDto.getFlightNumber(),
                result.airports().stream().findFirst().get(),
                result.airports().stream().skip(1).findFirst().get(),
                result.departureTime(),
                result.arrivalTime()
        )) {
            throw new FlightScheduleException("flight.exists");
        }

        // create the flight, save it in the database
        Flight savedFlight = flightRepository.save(
                flightDtoMapper.toFlight(flightDto, result.airports(), result.services())
        );

        // send a message to the Kafka topic
        kafkaOperations.send(mailTopic, SendMailMessage.builder()
                .subject("Flight created")
                .text("Flight with number " + savedFlight.getFlightNumber() + " has been created.")
                .build());

        // return the flight details
        return flightDetailsDtoMapper.toFlightDetailsDto(savedFlight);
    }

    /**
     * Get the details of a flight with the provided flightId.
     *
     * @param flightId flight ID
     * @return flight details DTO
     */
    public FlightDetailsDto getFlightDetails(Long flightId) {
        return flightDetailsDtoMapper.toFlightDetailsDto(
                flightRepository.findById(flightId).orElseThrow(
                        () -> new FlightNotFoundException("not.found.flight")
                )
        );
    }

    /**
     * List flights based on the provided filters and pagination info.
     *
     * @param flightListRequest flight list request
     * @param pageable          pageable
     * @return page of flight DTOs
     */
    public Page<ListFlightDto> listFlights(FlightListRequest flightListRequest, Pageable pageable) {
        Set<Airport> airports = airportService.findAirportsByCodes(
                flightListRequest.getDepartureAirport(),
                flightListRequest.getArrivalAirport()
        );

        Optional<Airport> departureAirport = airports.stream().findFirst();
        Optional<Airport> arrivalAirport = airports.stream().skip(1).findFirst();

        Set<String> services = flightListRequest.getServices();
        Set<Service> requestedServices;

        if (services != null) {
            requestedServices = serviceService.findServicesByNames(services);
            if (requestedServices.size() != services.size()) {
                throw new ServiceNotFoundException("not.found.services");
            }
        } else {
            requestedServices = Set.of();
        }

        return flightRepository.findByFilters(
                        departureAirport.map(Airport::getId).orElse(null),
                        arrivalAirport.map(Airport::getId).orElse(null),
                        !requestedServices.isEmpty() ? requestedServices.stream().map(Service::getId).collect(Collectors.toSet()) : null,
                        pageable)
                .map(flightDtoMapper::toListFlightDto);
    }

    /**
     * Update the details of a flight with the provided flightId.
     *
     * @param flightId  flight ID
     * @param flightDto flight DTO
     * @return flight details DTO
     */
    @Transactional
    public FlightDetailsDto updateFlight(Long flightId, FlightDto flightDto) {
        // validate the flight request
        FlightRequestValidationResult result = getFlightRequestValidationResult(flightDto);

        Optional<Flight> flightWithRequestedScheduling = flightRepository.findByFlightDetails(
                flightDto.getFlightNumber(),
                result.airports().stream().findFirst().get(),
                result.airports().stream().skip(1).findFirst().get(),
                result.departureTime(),
                result.arrivalTime()
        );

        // ensure that flight with the requested scheduling does not exist, or it is the same flight
        if (flightWithRequestedScheduling.isPresent() && !flightWithRequestedScheduling.get().getId().equals(flightId)) {
            throw new FlightScheduleException("invalid.update.flight");
        }

        // get the flight details
        Flight flight = flightRepository.findById(flightId).orElseThrow(
                () -> new FlightNotFoundException("not.found.flight")
        );

        // update the flight details
        flightDtoMapper.updateFlight(flight, flightDto, result.airports(), result.services());

        // create the flight, save it in the database and return its details
        return flightDetailsDtoMapper.toFlightDetailsDto(
                flightRepository.save(flight)
        );
    }

    /**
     * Delete a flight with the provided flightId.
     *
     * @param flightId flight ID
     */
    @Transactional
    public void deleteFlight(Long flightId) {
        // ensure that the flight exists
        if (!flightRepository.existsById(flightId)) {
            throw new FlightNotFoundException("not.found.flight");
        }

        // delete the flight
        flightRepository.deleteById(flightId);
    }

    /**
     * Upload flights from the provided file and save them in the database.
     *
     * @param file file containing flights
     * @return upload response
     */
    @Transactional
    public UploadResponse uploadFlights(MultipartFile file) throws Exception {
        // parse the flights from the file
        FlightJsonParser.ParseResult flightsParseResult = flightJsonParser.parseFlights(file);

        // save the flights in the database
        List<FlightDto> flights = flightsParseResult.flights();
        int succeeded = flightsParseResult.succeeded();
        int failed = flightsParseResult.failed();

        for (FlightDto flight : flights) {
            try {
                saveFlight(flight);
            } catch (Exception e) {
                succeeded--;
                failed++;
            }
        }

        // return the upload response
        return UploadResponse.builder()
                .succeeded(String.valueOf(succeeded))
                .failed(String.valueOf(failed))
                .build();
    }

    /**
     * Private method to validate the flight request.
     *
     * @param flightDto flight DTO
     * @return flight request validation result
     */
    private FlightRequestValidationResult getFlightRequestValidationResult(FlightDto flightDto) {
        // ensure that arrival time and departure time are valid
        Instant departureTime = Instant.parse(flightDto.getDepartureTime());
        Instant arrivalTime = Instant.parse(flightDto.getArrivalTime());

        if (arrivalTime.isBefore(departureTime) || departureTime.isBefore(Instant.now())) {
            throw new FlightScheduleException("invalid.arrival.departure.time");
        }

        Set<Airport> airports = airportService.findAirportsByCodes(
                flightDto.getDepartureAirport(),
                flightDto.getArrivalAirport()
        );

        // ensure that requested airports exist
        if (airports.size() < 2) {
            throw new AirportNotFoundException("not.found.airports");
        }

        // ensure that services exist
        Set<Service> services = serviceService.findServicesByNames(flightDto.getServices());

        if (services.size() != flightDto.getServices().size()) {
            throw new ServiceNotFoundException("not.found.services");
        }
        return new FlightRequestValidationResult(departureTime, arrivalTime, airports, services);
    }

    /**
     * Private record to hold the result of the flight request validation
     */
    private record FlightRequestValidationResult(Instant departureTime, Instant arrivalTime, Set<Airport> airports,
                                                 Set<Service> services) {
    }
}
