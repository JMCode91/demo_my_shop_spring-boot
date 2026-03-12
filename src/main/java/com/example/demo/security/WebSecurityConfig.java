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

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. Acceso a recursos estáticos
                        .requestMatchers("/resources/**", "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**").permitAll()

                        // 2. URLs públicas (HEMOS AÑADIDO "/error" y "/api/**" AL FINAL)
                        .requestMatchers("/", "/registration", "/login", "/new-user", "/product/**", "/error", "/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // 3. URLs solo para el Administrador
                        .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                        .failureUrl(LOGIN_FAILURE_URL)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                        .permitAll()
                );

        return http.build();
    }
}