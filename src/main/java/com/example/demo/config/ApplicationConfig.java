package com.example.demo.config;

import com.example.demo.interceptor.RequestLoggingInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Configuración maestra del entorno Web/MVC de la aplicación.
 * Centraliza la definición de Beans utilitarios, la internacionalización (idiomas),
 * el registro de interceptores de tráfico y la gestión de rutas de almacenamiento de archivos.
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    /**
     * Define el mapeador utilitario ModelMapper para conversión entre Entidades y DTOs.
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * Configura el mecanismo de resolución de idioma de la web.
     * Define por defecto el idioma Español de España ("es_ES") almacenado a través de cookies.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.of("es", "ES"));
        return localeResolver;
    }

    /**
     * Interceptor que evalúa si los parámetros de las URLs solicitan 
     * un cambio explícito de idioma a través del atributo '?lang='.
     */
    @Bean
    @NonNull
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    /**
     * Registra y configura los interceptores de la aplicación dentro del pipeline de Spring.
     * Gestiona tanto el cambio dinámico de idioma como el volcado de logs de peticiones.
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 1. Interceptor de Idioma
        registry.addInterceptor(localeChangeInterceptor());
        
        // 2. Interceptor de Auditoría/Logs (Excluyendo recursos estáticos para optimizar rendimiento)
        registry.addInterceptor(requestLoggingInterceptor)
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico");
    }

    /**
     * Configura los manejadores de recursos estáticos del servidor.
     * Mapea las peticiones virtuales de imágenes tanto a la carpeta estática del proyecto
     * como a un directorio físico 'uploads' externo en el disco duro del servidor.
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + uploadPath + "/", "classpath:/static/images/");
    }
}