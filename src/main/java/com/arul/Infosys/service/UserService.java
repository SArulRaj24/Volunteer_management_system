package com.arul.Infosys.service;

import com.arul.Infosys.dto.MessageResponse;
import com.arul.Infosys.dto.UserRequest;
import com.arul.Infosys.model.UserDetails;
import com.arul.Infosys.repo.UserDetailsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDetailsRepository repo;

    public UserService(UserDetailsRepository repo) {
        this.repo = repo;
    }

    // Validate role
    private void validateRole(String role) {
        if (role == null ||
                !(role.equalsIgnoreCase("VOLUNTEER") || role.equalsIgnoreCase("ORGANIZER"))) {
            throw new RuntimeException("Invalid role! Allowed values: VOLUNTEER, ORGANIZER");
        }
    }

    // Register
    public MessageResponse register(UserRequest req) {
        validateRole(req.getRole());

        if (repo.existsById(req.getEmailId())) {
            return new MessageResponse("User already exists!");
        }

        UserDetails user = new UserDetails();
        user.setEmailId(req.getEmailId());
        user.setPassword(req.getPassword());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setRole(req.getRole().toUpperCase());

        repo.save(user);

        return new MessageResponse("Registration successful!");
    }

    // Login
    public MessageResponse login(UserRequest req) {
        UserDetails user = repo.findById(req.getEmailId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new MessageResponse("Login successful!");
    }

    // Update (phone + address)
    public MessageResponse update(UserRequest req) {
        UserDetails user = repo.findById(req.getEmailId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (req.getPhone() != null)
            user.setPhone(req.getPhone());

        if (req.getAddress() != null)
            user.setAddress(req.getAddress());

        repo.save(user);

        return new MessageResponse("User updated successfully!");
    }

    // Reset password
    public MessageResponse resetPassword(UserRequest req) {
        UserDetails user = repo.findById(req.getEmailId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(req.getPassword());
        repo.save(user);

        return new MessageResponse("Password reset successful!");
    }

    // Profile
    public UserDetails getProfile(String emailId) {
        return repo.findById(emailId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Logout
    public MessageResponse logout(String emailId) {
        if (!repo.existsById(emailId))
            throw new RuntimeException("User not found");

        return new MessageResponse("Logout successful!");
    }
}
