package com.arul.Infosys.interceptor;

import com.arul.Infosys.exception.NotLoggedInException;
import com.arul.Infosys.exception.SessionExpiredException;
import com.arul.Infosys.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String path = request.getRequestURI();

        // ✅ EXCLUDED ENDPOINTS (NO SESSION REQUIRED)
        if (path.startsWith("/user/login")
                || path.startsWith("/user/register")
                || path.startsWith("/user/logout")
                ||path.endsWith("/event/checkin")) {
            return true;
        }

        // 🔒 SESSION REQUIRED FOR ALL OTHER ENDPOINTS
        String sessionId = request.getHeader("X-SESSION-ID");

        if (sessionId == null || sessionId.isBlank()) {
            throw new NotLoggedInException("Session ID missing");
        }

        if (!sessionService.isSessionValid(sessionId)) {
            throw new SessionExpiredException("Session expired, please login again");
        }

        return true;
    }
}