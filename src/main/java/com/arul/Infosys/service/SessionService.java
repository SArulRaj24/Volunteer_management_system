package com.arul.Infosys.service;

import com.arul.Infosys.exception.NotLoggedInException;
import com.arul.Infosys.exception.SessionExpiredException;
import com.arul.Infosys.model.UserSession;
import com.arul.Infosys.repo.UserSessionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    private final UserSessionRepository sessionRepo;


    private static final long SESSION_TTL_MINUTES = 120; // 2 hours

    public SessionService(UserSessionRepository sessionRepo) {
        this.sessionRepo = sessionRepo;
    }



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

    public void invalidateSession(String sessionId) {

        sessionRepo.findById(sessionId).ifPresent(sess -> {
            sess.setActive(false);
            sessionRepo.save(sess);
        });
    }


    public boolean isSessionValid(String sessionId) {

        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }

        UserSession sess = sessionRepo.findById(sessionId).orElse(null);

        if (sess == null || !sess.isActive()) {
            return false;
        }

        if (sess.getExpiresAt().isBefore(LocalDateTime.now())) {
            sess.setActive(false);
            sessionRepo.save(sess);
            return false;
        }

        return true;
    }


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
            sess.setActive(false);
            sessionRepo.save(sess);
            throw new SessionExpiredException("Session has expired. Please login again.");
        }

        return sess;
    }

    public void logoutByEmail(String emailId) {

        List<UserSession> sessions = sessionRepo.findByEmailId(emailId);

        if (sessions.isEmpty()) {
            throw new RuntimeException("No session found for email: " + emailId);
        }

        for (UserSession s : sessions) {
            s.setActive(false);
            s.setExpiresAt(LocalDateTime.now());
        }

        sessionRepo.saveAll(sessions);
    }


}
