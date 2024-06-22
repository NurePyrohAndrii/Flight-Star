package com.profITsoft.flightsystem.repository;

import com.profITsoft.flightsystem.entity.Airport;
import com.profITsoft.flightsystem.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

/**
 * Flight repository interface to interact with the database
 */
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Check if a flight with the provided details exists in the database
     *
     * @param flightNumber flight number
     * @param departureAirport departure airport
     * @param arrivalAirport arrival airport
     * @param departureTime departure time
     * @param arrivalTime arrival time
     * @return true if a flight with the provided details exists, false otherwise
     */
    @Query("""
           SELECT COUNT(f) > 0
            FROM Flight f
            WHERE f.flightNumber = :flightNumber
            AND f.departureAirport = :departureAirport
            AND f.arrivalAirport = :arrivalAirport
            AND f.departureTime = :departureTime
            AND f.arrivalTime = :arrivalTime
           """)
    boolean existsByFlightDetails(String flightNumber, Airport departureAirport, Airport arrivalAirport, Instant departureTime, Instant arrivalTime);

    /**
     * Find a flight with the provided details
     *
     * @param flightNumber flight number
     * @param departureAirport departure airport
     * @param arrivalAirport arrival airport
     * @param departureTime departure time
     * @param arrivalTime arrival time
     * @return flight with the provided details, if found
     */
    @Query("""
           SELECT f
            FROM Flight f
            WHERE f.flightNumber = :flightNumber
            AND f.departureAirport = :departureAirport
            AND f.arrivalAirport = :arrivalAirport
            AND f.departureTime = :departureTime
            AND f.arrivalTime = :arrivalTime
           """)
    Optional<Flight> findByFlightDetails(String flightNumber, Airport departureAirport, Airport arrivalAirport, Instant departureTime, Instant arrivalTime);

    /**
     * Find flights by provided filters
     *
     * @param departureAirportId departure airport id to filter by
     * @param arrivalAirportId arrival airport id to filter by
     * @param serviceIds service ids to filter by
     * @param pageable pagination information
     * @return page of flights matching the provided filters
     */
    @Query("""
        SELECT DISTINCT f FROM Flight f
        JOIN f.departureAirport da
        JOIN f.arrivalAirport aa
        JOIN f.services s
        WHERE (:departureAirportId IS NULL OR da.id = :departureAirportId)
          AND (:arrivalAirportId IS NULL OR aa.id = :arrivalAirportId)
          AND (:serviceIds IS NULL OR s.id IN :serviceIds)
        """)
    Page<Flight> findByFilters(
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            @Param("serviceIds") Set<Long> serviceIds,
            Pageable pageable);

    /**
     * Find a flight by its flight number
     *
     * @param number flight number
     * @return flight with the provided flight number, if found
     */
    Optional<Flight> findByFlightNumber(String number);
}
