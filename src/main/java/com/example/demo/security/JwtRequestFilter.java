package com.example.demo.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService; // Spring usará aquí tu MyShopUserDetailService automáticamente

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. Buscamos la cabecera Authorization en la petición
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 2. Comprobamos que no sea nula y empiece por "Bearer "
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7); // Recortamos los primeros 7 caracteres ("Bearer ")
            try {
                username = jwtTokenUtil.getUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("No se pudo obtener el token JWT");
            } catch (ExpiredJwtException e) {
                logger.warn("El token JWT ha expirado");
            }
        } else {
            // Es normal que salga este aviso si intentas hacer login o registrarte,
            // porque en ese momento ¡aún no tienes token!
            logger.warn("El token no viene con Bearer delante o no hay token");
        }

        // 3. Si hemos sacado un usuario del token y no está ya validado en este hilo...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Buscamos el usuario en la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Validamos que el token pertenece a este usuario y no ha caducado
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                // 5. ¡Le damos el pase VIP a Spring Security para que le deje entrar!
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        // 6. Siga circulando: pasamos al siguiente filtro o al Controlador final
        chain.doFilter(request, response);
    }
}