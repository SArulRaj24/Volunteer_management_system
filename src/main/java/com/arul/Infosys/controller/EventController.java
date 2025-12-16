package com.arul.Infosys.controller;

import com.arul.Infosys.dto.*;
import com.arul.Infosys.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EventCreateRequestDTO dto) {

        Long id = service.createEvent(dto);
        return ResponseEntity.ok(Map.of("Status", "created", "eventId", id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody EventUpdateRequestDTO dto) {

        Long id = service.updateEvent(dto);
        return ResponseEntity.ok(Map.of("Status", "updated successfully", "eventId", id));
    }

    @GetMapping("/list")
    public List<EventResponseDTO> list(@RequestParam Boolean status) {
        return service.listEvents(status);
    }

    @GetMapping("/list/{eventId}")
    public EventResponseDTO get(@PathVariable Long eventId) {
        return service.getEventById(eventId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long eventId) {

        service.deleteEvent(eventId);
        return ResponseEntity.ok(Map.of("Status", "deleted successfully"));
    }
    @PostMapping("/feedback")
    public Map<String, String> feedback(@RequestBody FeedbackDTO dto) {

        service.submitFeedback(dto);

        return Map.of(
                "Status", "success",
                "Message", "Thanks for rating!"
        );
    }
}
