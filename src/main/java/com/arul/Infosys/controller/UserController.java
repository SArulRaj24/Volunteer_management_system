package com.arul.Infosys.controller;

import com.arul.Infosys.dto.MessageResponse;
import com.arul.Infosys.dto.UserRequest;
import com.arul.Infosys.model.UserDetails;
import com.arul.Infosys.service.UserService;
import com.arul.Infosys.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@RequestBody UserRequest req, HttpServletRequest request) {
        // authenticate user
        MessageResponse resp = userService.login(req);

        // create HttpSession and persist it in DB
        HttpSession httpSession = request.getSession(true); // create if absent
        sessionService.createSession(httpSession, req.getEmailId());

        return ResponseEntity.ok(new MessageResponse("Login successful; session created"));
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponse> update(@RequestBody UserRequest req, HttpServletRequest request) {
        // Interceptor already validated session; additionally ensure session user matches request email
        HttpSession httpSession = request.getSession(false);
        String sessionId = httpSession.getId();
        // ensure the session corresponds to the user performing change
        // validate session -> will not throw if valid
        var sess = sessionService.validateSessionOrThrow(httpSession);
        if (!sess.getEmailId().equals(req.getEmailId())) {
            return ResponseEntity.status(403).body(new MessageResponse("Cannot modify another user's profile"));
        }
        return ResponseEntity.ok(userService.update(req));
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody UserRequest req, HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        var sess = sessionService.validateSessionOrThrow(httpSession);
        if (!sess.getEmailId().equals(req.getEmailId())) {
            return ResponseEntity.status(403).body(new MessageResponse("Cannot change password for another user"));
        }
        return ResponseEntity.ok(userService.resetPassword(req));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetails> profile(@RequestParam String emailId, HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        var sess = sessionService.validateSessionOrThrow(httpSession);
        if (!sess.getEmailId().equals(emailId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(userService.getProfile(emailId));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody UserRequest req, HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            String sid = httpSession.getId();
            // deactivate DB session
            sessionService.invalidateSession(sid);
            // invalidate server session
            httpSession.invalidate();
        }
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}
