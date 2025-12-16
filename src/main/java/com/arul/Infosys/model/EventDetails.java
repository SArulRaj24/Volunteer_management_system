package com.arul.Infosys.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "event_details")
public class EventDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String eventName;
    private String description;
    private String address;
    private String city;
    private String organizerId;

    private Integer maxAllowedRegistrations;
    private LocalDate eventStartDate;
    private LocalDate eventEndDate;

    private Float rating;
    private Boolean registrationAllowed;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public EventDetails() {}

    // ✔ FULL CONSTRUCTOR – matches DB & fixes your earlier error
    public EventDetails(
            Long eventId,
            String eventName,
            String description,
            String address,
            String city,
            String organizerId,
            Integer maxAllowedRegistrations,
            LocalDate eventStartDate,
            LocalDate eventEndDate,
            Float rating,
            Boolean registrationAllowed
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.address = address;
        this.city = city;
        this.organizerId = organizerId;
        this.maxAllowedRegistrations = maxAllowedRegistrations;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.rating = rating;
        this.registrationAllowed = registrationAllowed;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    // Getters and Setters (generate using IDE)
}
