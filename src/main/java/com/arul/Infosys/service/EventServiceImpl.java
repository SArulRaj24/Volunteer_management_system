package com.arul.Infosys.service;

import com.arul.Infosys.dto.*;
import com.arul.Infosys.exception.EventNotFoundException;
import com.arul.Infosys.exception.ResourceNotFoundException;
import com.arul.Infosys.model.EventDetails;
import com.arul.Infosys.repo.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    public EventServiceImpl(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long createEvent(EventCreateRequestDTO dto, String creatorEmail) {

        EventDetails event = new EventDetails();

        event.setEventName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setAddress(dto.getAddress());
        event.setCity(dto.getCity());
        event.setEventStartDate(dto.getStartDate());
        event.setEventEndDate(dto.getEndDate());
        event.setMaxAllowedRegistrations(dto.getMaximumAllowedRegistrations());
        event.setRegistrationAllowed(dto.getRegistrationAllowed());
        event.setCreatedAt(LocalDate.now());
        event.setCreatedBy(creatorEmail);


        return repository.save(event).getEventId();
    }

    @Override
    public Long updateEvent(EventUpdateRequestDTO dto) {

        EventDetails event = repository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (dto.getName() != null)
            event.setEventName(dto.getName());

        if (dto.getAddress() != null)
            event.setAddress(dto.getAddress());

        if (dto.getCity() != null)
            event.setCity(dto.getCity());

        if (dto.getStartDate() != null)
            event.setEventStartDate(dto.getStartDate());

        if (dto.getEndDate() != null)
            event.setEventEndDate(dto.getEndDate());

        if (dto.getMaximumAllowedRegistrations() != null)
            event.setMaxAllowedRegistrations(dto.getMaximumAllowedRegistrations());

        if (dto.getRegistrationAllowed() != null)
            event.setRegistrationAllowed(dto.getRegistrationAllowed());

        event.setModifiedAt(LocalDate.now());

        repository.save(event);
        return event.getEventId();
    }

    @Override
    public List<EventResponseDTO> listEventsByType(String type) {

        LocalDate today = LocalDate.now();
        List<EventDetails> events;

        switch (type.toLowerCase()) {
            case "upcoming" ->
                    events = repository.findByEventStartDateAfter(today);
            case "ongoing" ->
                    events = repository
                            .findByEventStartDateLessThanEqualAndEventEndDateGreaterThanEqual(today, today);
            case "completed" ->
                    events = repository.findByEventEndDateBefore(today);
            default ->
                    throw new IllegalArgumentException("Use upcoming, ongoing, or completed");
        }

        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponseDTO getEventById(Long eventId) {

        EventDetails event = repository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        return mapToResponse(event);
    }

    @Override
    public void deleteEvent(Long eventId) {

        if (!repository.existsById(eventId))
            throw new EventNotFoundException("Event not found");

        repository.deleteById(eventId);
    }

    @Override
    public String submitFeedback(FeedbackDTO dto) {

        if (dto.getEventId() == null) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }

        EventDetails event = repository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setRating(dto.getRating().floatValue());
        event.setModifiedAt(LocalDate.now());

        repository.save(event);

        return "Thanks for rating!";
    }


    private EventResponseDTO mapToResponse(EventDetails event) {

        EventResponseDTO dto = new EventResponseDTO();
        dto.setEventId(event.getEventId());
        dto.setName(event.getEventName());
        dto.setDescription(event.getDescription());
        dto.setAddress(event.getAddress());
        dto.setCity(event.getCity());
        dto.setStartDate(event.getEventStartDate());
        dto.setEndDate(event.getEventEndDate());
        dto.setVolunteersNeeded(event.getMaxAllowedRegistrations());
        dto.setRegistrationAllowed(event.getRegistrationAllowed());
        dto.setRating(event.getRating());

        return dto;
    }
}
