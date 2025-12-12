package com.arul.Infosys.config;

import com.arul.Infosys.interceptor.SessionInterceptor;
import com.arul.Infosys.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionService sessionService;

    @Autowired
    public WebConfig(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // protect specific endpoints: update, resetPassword, profile, logout
        registry.addInterceptor(new SessionInterceptor(sessionService))
                .addPathPatterns("/user/update", "/user/resetPassword", "/user/profile", "/user/logout");
    }

    // CORS config if you use Postman or front-end from different origin (optional)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
