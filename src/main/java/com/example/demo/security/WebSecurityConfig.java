package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Importamos las constantes que acabas de crear
import static com.example.demo.security.Constants.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // FIGURA 72-C: El Encriptador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // FIGURA 72-B y 72-C: Reglas de acceso y bloqueo de URLs
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF tal y como pide la Figura 72-B de tus apuntes
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // FIGURA 72-C: Permitimos el acceso a todo el contenido estático (imágenes, css, js)
                        .requestMatchers("/resources/**", "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**").permitAll()

                        // FIGURA 72-B: URLs públicas a las que cualquiera puede entrar sin login
                        .requestMatchers("/", "/registration", "/login", "/new-user", "/product/**").permitAll()

                        // FIGURA 72-B: URLs privadas solo para el Administrador
                        .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)


                        // Cualquier otra URL que no esté arriba, requerirá estar logueado
                        .anyRequest().authenticated()
                )

                // FIGURA 72-B: Configuración de la página de Login
                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                        .failureUrl(LOGIN_FAILURE_URL)
                        .permitAll()
                )

                // FIGURA 72-B: Configuración del botón de Salir (Logout)
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)  // <--- ¡ESTA ES LA NUEVA LÍNEA CORTA Y MODERNA!
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                        .permitAll()
                );

        return http.build();
    }
}