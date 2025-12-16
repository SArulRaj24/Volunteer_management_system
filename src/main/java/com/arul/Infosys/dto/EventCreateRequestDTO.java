package com.arul.Infosys.dto;

import lombok.Data;

import java.time.LocalDate;

@Data

public class EventRequestDTO {
    public Long eventId;
    public String name;
    public String description;
    public String address;
    public String city;
    public LocalDate startDate;
    public LocalDate endDate;
    public Integer maximumAllowedRegistrations;
    public String organizer;
    public Boolean registrationAllowed;
}
