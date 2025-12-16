package com.arul.Infosys.service;

import com.arul.Infosys.dto.*;

import java.util.List;

public interface EventService {

    Long createEvent(EventCreateRequestDTO dto);

    Long updateEvent(EventUpdateRequestDTO dto);

    List<EventResponseDTO> listEvents(Boolean status);

    EventResponseDTO getEventById(Long eventId);

    void deleteEvent(Long eventId);

    String submitFeedback(FeedbackDTO feedbackDTO);
}
