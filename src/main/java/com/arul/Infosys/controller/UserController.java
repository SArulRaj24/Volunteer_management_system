package com.arul.Infosys.controller;

import com.arul.Infosys.dto.MessageResponse;
import com.arul.Infosys.dto.UserRequest;
import com.arul.Infosys.model.UserDetails;
import com.arul.Infosys.service.SessionService;
import com.arul.Infosys.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // Authenticate credentials
        UserDetails user = userService.login(req);

        // Create new HTTP Session (invalidating old one if exists ensures clean state)
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) oldSession.invalidate();

        HttpSession newSession = request.getSession(true);
        sessionService.createSession(newSession, user.getEmailId());

        return ResponseEntity.ok(new MessageResponse("Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Get user from session to deactivate DB record
            try {
                UserDetails user = sessionService.getLoggedInUser(session);
                sessionService.logoutByEmail(user.getEmailId());
            } catch (Exception ignored) {
                // Session might already be invalid, just proceed to invalidate HttpSession
            }
            session.invalidate();
        }
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponse> update(@RequestBody UserRequest req, HttpServletRequest request) {
        UserDetails currentUser = sessionService.getLoggedInUser(request.getSession(false));

        if (!currentUser.getEmailId().equals(req.getEmailId())) {
            return ResponseEntity.status(403).body(new MessageResponse("Unauthorized: Cannot update another user's profile"));
        }
        return ResponseEntity.ok(userService.update(req));
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody UserRequest req, HttpServletRequest request) {
        UserDetails currentUser = sessionService.getLoggedInUser(request.getSession(false));

        if (!currentUser.getEmailId().equals(req.getEmailId())) {
            return ResponseEntity.status(403).body(new MessageResponse("Unauthorized: Cannot reset another user's password"));
        }
        return ResponseEntity.ok(userService.resetPassword(req));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestParam String emailId, HttpServletRequest request) {
        UserDetails currentUser = sessionService.getLoggedInUser(request.getSession(false));

        if (!currentUser.getEmailId().equals(emailId)) {
            return ResponseEntity.status(403).body(new MessageResponse("Unauthorized: Cannot view another user's profile"));
        }
        return ResponseEntity.ok(userService.getProfile(emailId));
    }
}