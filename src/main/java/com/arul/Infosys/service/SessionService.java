package com.arul.Infosys.service;

import com.arul.Infosys.model.UserSession;
import com.arul.Infosys.repo.UserSessionRepository;
import com.arul.Infosys.exception.SessionExpiredException;
import com.arul.Infosys.exception.NotLoggedInException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class SessionService {

    private final UserSessionRepository sessionRepo;
    // session TTL in minutes (adjust as desired)
    private static final long SESSION_TTL_MINUTES = 120; // 2 hours

    public SessionService(UserSessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    /**
     * Create a DB record for current HttpSession.
     */
    public UserSession createSession(HttpSession httpSession, String emailId) {
        String sid = httpSession.getId();
        UserSession s = new UserSession();
        s.setSessionId(sid);
        s.setEmailId(emailId);
        s.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_TTL_MINUTES));
        s.setActive(true);
        sessionRepo.save(s);
        return s;
    }

    /**
     * Mark session as inactive in DB (logout).
     */
    public void invalidateSession(String sessionId) {
        sessionRepo.findById(sessionId).ifPresent(sess -> {
            sess.setActive(false);
            sessionRepo.save(sess);
        });
    }

    /**
     * Validate sessionId: exists, active, not expired.
     * Throws NotLoggedInException or SessionExpiredException on failure.
     */
    public UserSession validateSessionOrThrow(HttpSession httpSession) {
        if (httpSession == null) {
            throw new NotLoggedInException("No session. Please login first.");
        }
        String sid = httpSession.getId();
        UserSession sess = sessionRepo.findById(sid)
                .orElseThrow(() -> new NotLoggedInException("Session not found. Please login."));

        if (!sess.isActive()) {
            throw new NotLoggedInException("Session is not active. Please login.");
        }
        if (sess.getExpiresAt().isBefore(LocalDateTime.now())) {
            // mark inactive
            sess.setActive(false);
            sessionRepo.save(sess);
            throw new SessionExpiredException("Session has expired. Please login again.");
        }
        return sess;
    }
}
