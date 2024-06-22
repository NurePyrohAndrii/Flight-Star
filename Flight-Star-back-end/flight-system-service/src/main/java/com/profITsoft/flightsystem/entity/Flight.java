package com.profITsoft.flightsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

/**
 * Flight entity class to represent a flight
 */
@Entity
@Table(name = "flight")
@Getter
@Setter
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number")
    private String flightNumber;

    @ManyToOne
    @JoinColumn(name = "departure_airport")
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(name = "arrival_airport")
    private Airport arrivalAirport;

    @Column(name = "departure_time")
    private Instant departureTime;

    @Column(name = "arrival_time")
    private Instant arrivalTime;

    @ManyToMany
    @JoinTable(
            name = "flight_service",
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services;
}
