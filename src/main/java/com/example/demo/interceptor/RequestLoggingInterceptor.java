package com.example.demo.interceptor; // Ajusta esto a tu paquete real

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    // Creamos el "bolígrafo" con el que vamos a escribir en la consola
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    // Este método se ejecuta SIEMPRE antes de llegar a cualquier Controlador
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String method = request.getMethod(); // GET, POST, DELETE...
        String uri = request.getRequestURI(); // /api/users, /login, /home...
        String clientIp = request.getRemoteAddr(); // La IP desde donde se conectan

        // Vamos a cotillear si el usuario está logueado (ya sea por Web o por Token JWT)
        String username = "Anónimo";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            username = authentication.getName(); // Sacamos su nombre de usuario
        }

        // Escribimos en la consola la información bonita y formateada
        logger.info("Navegación -> Usuario: [{}] | IP: [{}] | Método: [{}] | Ruta: [{}]", username, clientIp, method, uri);

        // Devolvemos true para decirle a Spring: "Ya he tomado nota, deja que la petición continúe su camino"
        return true;
    }
}