package com.example.demo.interceptor; 

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de tráfico HTTP encargado de la telemetría y auditoría del sistema.
 * Se ejecuta automáticamente antes de que cualquier petición alcance los controladores.
 * Registra en los logs del servidor la IP, el usuario autenticado y la ruta solicitada.
 */
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

        // Extraemos el nombre de usuario si está logueado en Spring Security
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName(); 
        }

        logger.info("Navegación -> Usuario: [{}] | IP: [{}] | Método: [{}] | Ruta: [{}]", username, clientIp, method, uri);

        return true; // Permite que la petición continúe su ciclo de vida normal
    }
}