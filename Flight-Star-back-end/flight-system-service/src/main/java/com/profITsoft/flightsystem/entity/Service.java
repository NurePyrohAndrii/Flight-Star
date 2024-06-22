package com.profITsoft.flightsystem.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Service entity class to represent the services provided within airlines.
 */
@Entity
@Table(name = "service")
@Getter
@Setter
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the service provided by the airlines. Unique value.
     */
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}