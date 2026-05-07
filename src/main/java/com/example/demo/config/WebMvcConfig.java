package com.example.demo.config; // O el paquete donde lo hayas creado

import com.example.demo.interceptor.RequestLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; // Importación añadida
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer { // ¡Fíjate en esta interfaz!

    @Autowired
    @NonNull // Le aseguramos al editor que Spring lo inyectará y no será nulo
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) { // Prometemos que no es nulo
        registry.addInterceptor(requestLoggingInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico");
    }
}