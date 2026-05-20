package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.example.demo.security.Constants.*;

/**
 * Clase principal de configuración de seguridad de la aplicación.
 * Define las reglas de acceso (Rutas públicas vs privadas), la encriptación de contraseñas
 * y la gestión del inicio y cierre de sesión mediante formularios HTML (Cookies de sesión).
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Define el algoritmo de encriptación para las contraseñas de los usuarios.
     * BCrypt es el estándar actual más seguro recomendado por Spring Security.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el filtro de seguridad HTTP.
     * Aquí se establecen los "porteros" de la aplicación: quién puede entrar y a dónde.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desactivamos CSRF (opcional para desarrollo, recomendable activar en prod para formularios)
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. Acceso libre a recursos estáticos (CSS, JS, imágenes)
                        .requestMatchers("/resources/**", "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**").permitAll()

                        // 2. URLs totalmente públicas (No requieren inicio de sesión)
                        .requestMatchers(
                                "/", 
                                "/registration", 
                                "/new-user", 
                                "/login", 
                                "/error",
                                // -- RUTAS DEL CATÁLOGO ABIERTAS --
                                "/catalog",
                                "/category/**",
                                "/search",
                                "/ofertas",
                                "/product/**",
                                "/.well-known/**",
                                "/api/**"
                        ).permitAll()

                        // 3. URLs restringidas exclusivamente al rol de Administrador
                        .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)

                        // 4. Cualquier otra petición requiere que el usuario esté logueado (Ej: /cart, /profile)
                        .anyRequest().authenticated()
                )

                // Configuración del formulario de inicio de sesión de Thymeleaf
                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL, true)
                        .failureUrl(LOGIN_FAILURE_URL)
                        .permitAll()
                )

                // Configuración del cierre de sesión
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                        .permitAll()
                );

        return http.build();
    }
}