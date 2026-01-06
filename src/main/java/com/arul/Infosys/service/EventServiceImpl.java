package com.arul.Infosys.service;

import com.arul.Infosys.dto.*;
import com.arul.Infosys.exception.EventNotFoundException;
import com.arul.Infosys.exception.ResourceNotFoundException;
import com.arul.Infosys.model.EventDetails;
import com.arul.Infosys.model.RegistrationDetails;
import com.arul.Infosys.repo.EventRepository;
import com.arul.Infosys.repo.RegistrationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final RegistrationRepository registrationRepository;
    public EventServiceImpl(EventRepository repository,RegistrationRepository registrationRepository) {
        this.repository = repository;
        this.registrationRepository = registrationRepository;

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
    @Transactional
    public void deleteEvent(Long eventId) {

        if (!repository.existsById(eventId))
            throw new EventNotFoundException("Event not found");
        registrationRepository.deleteByEventId(eventId);
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

    //register

    @Override
    public String registerEvent(Long eventId, String emailId) {

        EventDetails event = repository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getRegistrationAllowed()) {
            throw new IllegalStateException("Registrations closed");
        }

        registrationRepository
                .findByVolunteerIdAndEventId(emailId, eventId)
                .ifPresentOrElse(
                        reg -> {
                            if ("REGISTERED".equals(reg.getStatus())) {
                                throw new IllegalStateException("Already registered");
                            }
                            reg.setStatus("REGISTERED");
                            reg.setModifiedAt(LocalDateTime.now());
                            registrationRepository.save(reg);
                        },
                        () -> {
                            RegistrationDetails reg = new RegistrationDetails();
                            reg.setVolunteerId(emailId);
                            reg.setEventId(eventId);
                            reg.setStatus("REGISTERED");
                            reg.setCheckIn(false);
                            reg.setCreatedAt(LocalDateTime.now());
                            reg.setModifiedAt(LocalDateTime.now());
                            registrationRepository.save(reg);
                        }
                );

        return "registered";
    }

    /* ================= UNREGISTER ================= */

    @Override
    public String unregisterEvent(Long eventId, String emailId) {

        RegistrationDetails reg = registrationRepository
                .findByVolunteerIdAndEventId(emailId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        reg.setStatus("WITHDRAWN");
        reg.setModifiedAt(LocalDateTime.now());

        registrationRepository.save(reg);
        return "un-registered";
    }

    /* ================= CHECK-IN ================= */

    @Override
    public String checkIn(Long eventId, String emailId) {

        if (eventId == null || emailId == null || emailId.isBlank()) {
            throw new IllegalArgumentException("EventId and EmailId are required");
        }

        RegistrationDetails reg = registrationRepository
                .findByVolunteerIdAndEventId(emailId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Not registered"));

        if ("WITHDRAWN".equals(reg.getStatus())) {
            throw new IllegalStateException("User withdrawn");
        }

        if (Boolean.TRUE.equals(reg.getCheckIn())) {
            throw new IllegalStateException("Already checked in");
        }

        reg.setCheckIn(true);
        registrationRepository.save(reg);

        return "check in successful";
    }


    /* ================= REGISTRATIONS ================= */

    @Override
    public EventParticipantsResponse getRegistrations(Long eventId) {

        List<String> emails = registrationRepository
                .findByEventIdAndStatus(eventId, "REGISTERED")
                .stream()
                .map(RegistrationDetails::getVolunteerId)
                .toList();

        return new EventParticipantsResponse(emails.size(), emails);
    }

    /* ================= PARTICIPANTS ================= */

    @Override
    public EventParticipantsResponse getParticipants(Long eventId) {

        List<String> emails = registrationRepository
                .findByEventIdAndCheckInTrue(eventId)
                .stream()
                .map(RegistrationDetails::getVolunteerId)
                .toList();

        return new EventParticipantsResponse(emails.size(), emails);
    }
}