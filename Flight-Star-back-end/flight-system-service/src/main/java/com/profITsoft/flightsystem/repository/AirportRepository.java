package com.profITsoft.flightsystem.repository;

import com.profITsoft.flightsystem.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

/**
 * Airport repository class to manage airport data in a database
 */
public interface AirportRepository extends JpaRepository<Airport, String> {

    /**
     * Find airports by airport codes
     *
     * @param codes a set of airport codes to search for
     * @return set of airports
     */
    Set<Airport> findByAirportCodeIn(Set<String> codes);

    /**
     * Check if an airport with the provided code exists
     *
     * @param airportCode airport code
     */
    boolean existsByAirportCode(String airportCode);

    /**
     * Find an airport by its code
     *
     * @param airportCode airport code
     * @return airport
     */
    Optional<Airport> findByAirportCode(String airportCode);
}
