package com.arul.Infosys.service;

import com.arul.Infosys.exception.NotLoggedInException;
import com.arul.Infosys.exception.SessionExpiredException;
import com.arul.Infosys.exception.UserNotFoundException;
import com.arul.Infosys.model.UserDetails;
import com.arul.Infosys.model.UserSession;
import com.arul.Infosys.repo.UserDetailsRepository;
import com.arul.Infosys.repo.UserSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    private final UserSessionRepository sessionRepo;
    private final UserDetailsRepository userRepo;

    // Session valid for 2 hours
    private static final long SESSION_TTL_MINUTES = 120;

    public SessionService(UserSessionRepository sessionRepo, UserDetailsRepository userRepo) {
        this.sessionRepo = sessionRepo;
        this.userRepo = userRepo;
    }

    public void createSession(HttpSession httpSession, String emailId) {
        String sid = httpSession.getId();
        UserSession s = new UserSession();
        s.setSessionId(sid);
        s.setEmailId(emailId);
        s.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_TTL_MINUTES));
        s.setActive(true);
        sessionRepo.save(s);
    }

    // Main validation method used by Interceptor
    public boolean isSessionValid(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return false;

        return sessionRepo.findById(sessionId)
                .map(sess -> {
                    if (!sess.isActive()) return false;
                    if (sess.getExpiresAt().isBefore(LocalDateTime.now())) {
                        sess.setActive(false);
                        sessionRepo.save(sess);
                        return false;
                    }
                    return true;
                }).orElse(false);
    }

    // Helper to get Current User and validate session in one go (Used in Controllers)
    public UserDetails getLoggedInUser(HttpSession httpSession) {
        if (httpSession == null) {
            throw new NotLoggedInException("No active session found.");
        }

        String sid = httpSession.getId();
        UserSession sess = sessionRepo.findById(sid)
                .orElseThrow(() -> new NotLoggedInException("Session not found. Please login."));

        if (!sess.isActive()) throw new NotLoggedInException("Session inactive.");
        if (sess.getExpiresAt().isBefore(LocalDateTime.now())) {
            sess.setActive(false);
            sessionRepo.save(sess);
            throw new SessionExpiredException("Session expired.");
        }

        // Return full User object to check Roles
        return userRepo.findById(sess.getEmailId())
                .orElseThrow(() -> new UserNotFoundException("User associated with session not found."));
    }

    public void logoutByEmail(String emailId) {
        List<UserSession> sessions = sessionRepo.findByEmailId(emailId);
        sessions.forEach(s -> {
            s.setActive(false);
            s.setExpiresAt(LocalDateTime.now());
        });
        sessionRepo.saveAll(sessions);
    }
}