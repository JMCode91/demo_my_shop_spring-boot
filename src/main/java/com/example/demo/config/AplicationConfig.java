package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; // Importación obligatoria
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class AplicationConfig implements WebMvcConfigurer {

    // 1. Definimos que el idioma por defecto sea Español de España
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.of("es", "ES"));
        return localeResolver;
    }

    // 2. Definimos el "Interceptor" que vigilará si la URL trae el parámetro "lang"
    @Bean
    @NonNull // Prometemos que este método nunca devuelve nulo
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    // 3. Registramos ese interceptor en Spring para que empiece a funcionar
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) { // Prometemos que el parámetro no es nulo
        registry.addInterceptor(localeChangeInterceptor());
    }
}