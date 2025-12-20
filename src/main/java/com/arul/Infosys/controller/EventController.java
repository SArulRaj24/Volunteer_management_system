package com.arul.Infosys.controller;

import com.arul.Infosys.dto.*;
import com.arul.Infosys.service.EventService;
import com.arul.Infosys.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService service;
    private final SessionService sessionService;

    public EventController(EventService service, SessionService sessionService) {
        this.service = service;
        this.sessionService = sessionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EventCreateRequestDTO dto, HttpSession session) {
        var sess = sessionService.validateSessionOrThrow(session);
        String email = sess.getEmailId();
        Long id = service.createEvent(dto, email);
        return ResponseEntity.ok(Map.of("Status", "created", "eventId", id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody EventUpdateRequestDTO dto) {
        Long id = service.updateEvent(dto);
        return ResponseEntity.ok(Map.of("Status", "updated", "eventId", id));
    }

    @GetMapping("/list")
    public List<EventResponseDTO> list(@RequestParam String type) {
        return service.listEventsByType(type);
    }

    @GetMapping("/{eventId}")
    public EventResponseDTO get(@PathVariable Long eventId) {
        return service.getEventById(eventId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long eventId) {
        service.deleteEvent(eventId);
        return ResponseEntity.ok(Map.of("Status", "deleted successfully"));
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> feedback(@RequestBody FeedbackDTO dto) {
        return ResponseEntity.ok(
                Map.of("Status", "success", "Message", service.submitFeedback(dto))
        );
    }

    /* ---------------- REGISTER ---------------- */

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest req) {

        String result = service.registerEvent(
                req.getEventId(),
                req.getEmailId()
        );

        return ResponseEntity.ok(
                Map.of("status", result)
        );
    }

    /* ---------------- UNREGISTER ---------------- */

    @PostMapping("/unregister")
    public ResponseEntity<?> unregister(@RequestBody RegistrationRequest req) {

        String result = service.unregisterEvent(
                req.getEventId(),
                req.getEmailId()
        );

        return ResponseEntity.ok(
                Map.of("status", result)
        );
    }

    /* ---------------- CHECK-IN ---------------- */

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody RegistrationRequest req) {

        String result = service.checkIn(
                req.getEventId(),
                req.getEmailId()
        );

        return ResponseEntity.ok(
                Map.of("status", result)
        );
    }


    /* ---------------- REGISTRATIONS ---------------- */

    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<EventParticipantsResponse> registrations(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                service.getRegistrations(eventId)
        );
    }

    /* ---------------- PARTICIPANTS ---------------- */

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<EventParticipantsResponse> participants(
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok(
                service.getParticipants(eventId)
        );
    }
}

