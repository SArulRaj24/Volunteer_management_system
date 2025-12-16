package com.arul.Infosys.service;

import com.arul.Infosys.dto.*;
import com.arul.Infosys.exception.EventNotFoundException;
import com.arul.Infosys.exception.ResourceNotFoundException;
import com.arul.Infosys.model.EventDetails;
import com.arul.Infosys.repo.EventRepository;
import com.arul.Infosys.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;

    public EventServiceImpl(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long createEvent(EventCreateRequestDTO dto) {

        EventDetails event = new EventDetails(
                null,
                dto.getName(),
                dto.getDescription(),
                dto.getAddress(),
                dto.getCity(),
                dto.getOrganizer(),
                dto.getMaximumAllowedRegistrations(),
                dto.getStartDate(),
                dto.getEndDate(),
                null,
                dto.getRegistrationAllowed()
        );

        return repository.save(event).getEventId();
    }

    @Override
    public Long updateEvent(EventUpdateRequestDTO dto) {

        EventDetails event = repository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        event.setEventName(dto.getName());
        event.setAddress(dto.getAddress());
        event.setCity(dto.getCity());
        event.setEventStartDate(dto.getStartDate());
        event.setEventEndDate(dto.getEndDate());
        event.setMaxAllowedRegistrations(dto.getMaximumAllowedRegistrations());
        event.setRegistrationAllowed(dto.getRegistrationAllowed());

        repository.save(event);
        return event.getEventId();
    }

    @Override
    public List<EventResponseDTO> listEvents(Boolean status) {

        return repository.findByRegistrationAllowed(status)
                .stream()
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

        if (!repository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }
        repository.deleteById(eventId);
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



    @Override
    public String submitFeedback(FeedbackDTO dto) {

        EventDetails event = repository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setRating(dto.getRating().floatValue());
        event.setModifiedAt(LocalDateTime.now());

        repository.save(event);

        return "Thanks for rating!";
    }
}
