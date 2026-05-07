package com.example.demo.interceptor; 

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull; // Importación obligatoria
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        String method = request.getMethod(); 
        String uri = request.getRequestURI(); 
        String clientIp = request.getRemoteAddr(); 

        String username = "Anónimo";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName(); 
        }

        logger.info("Navegación -> Usuario: [{}] | IP: [{}] | Método: [{}] | Ruta: [{}]", username, clientIp, method, uri);

        return true;
    }
}