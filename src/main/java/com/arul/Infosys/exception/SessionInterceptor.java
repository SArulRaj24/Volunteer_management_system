package com.arul.Infosys.interceptor;

import com.arul.Infosys.service.SessionService;
import com.arul.Infosys.exception.NotLoggedInException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that ensures a valid DB-backed HttpSession exists for protected endpoints.
 */
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // allow OPTIONS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession httpSession = request.getSession(false);
        // throws custom exception if not valid - handled by GlobalExceptionHandler
        sessionService.validateSessionOrThrow(httpSession);
        return true;
    }
}
