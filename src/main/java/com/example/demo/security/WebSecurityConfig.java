package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.demo.security.Constants.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 1. Traemos al Guardia de Seguridad de los Tokens
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Traemos al Manager de autenticación (necesario para el ApiUserController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // 1. Acceso a recursos estáticos
                        .requestMatchers("/resources/**", "/static/**", "/templates/**", "/css/**", "/js/**", "/images/**", "/fonts/**", "/webjars/**").permitAll()

                        // 2. URLs públicas (OJO: Al tener "/api/**" aquí, nuestro nuevo /api/login ya es público por defecto)
                        .requestMatchers("/", "/registration", "/login", "/new-user", "/product/**", "/error", "/api/users/login", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // 3. URLs solo para el Administrador
                        .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)

                        .anyRequest().authenticated()
                )

                // Tu login visual de Thymeleaf (¡Se mantiene intacto!)
                .formLogin(form -> form
                        .loginPage(LOGIN_URL)
                        .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                        .failureUrl(LOGIN_FAILURE_URL)
                        .permitAll()
                )

                // Tu logout de Thymeleaf (¡Se mantiene intacto!)
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_URL)
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                        .permitAll()
                );

        // 3. Añadimos al Guardia del Token ANTES del guardia del formulario web
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}