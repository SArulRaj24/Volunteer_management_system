package com.arul.Infosys.controller;

import com.arul.Infosys.dto.MessageResponse;
import com.arul.Infosys.dto.UserRequest;
import com.arul.Infosys.model.UserDetails;
import com.arul.Infosys.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody UserRequest req) {
        return ResponseEntity.ok(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponse> login(@RequestBody UserRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @PutMapping("/update")
    public ResponseEntity<MessageResponse> update(@RequestBody UserRequest req) {
        return ResponseEntity.ok(service.update(req));
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody UserRequest req) {
        return ResponseEntity.ok(service.resetPassword(req));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetails> profile(@RequestParam String emailId) {
        return ResponseEntity.ok(service.getProfile(emailId));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody UserRequest req) {
        return ResponseEntity.ok(service.logout(req));
    }
}
