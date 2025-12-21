package com.arul.Infosys.interceptor;

import com.arul.Infosys.exception.NotLoggedInException;
import com.arul.Infosys.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String path = request.getRequestURI();

        // 1. Public Endpoints (No Auth Required)
        if (path.startsWith("/user/login") || path.startsWith("/user/register")) {
            return true;
        }

        // 2. Strict Session Check for everything else
        HttpSession session = request.getSession(false); // Do not create new session

        if (session == null) {
            throw new NotLoggedInException("Access Denied: No session provided. Please login.");
        }

        if (!sessionService.isSessionValid(session.getId())) {
            throw new NotLoggedInException("Access Denied: Session Invalid or Expired.");
        }

        return true;
    }
}