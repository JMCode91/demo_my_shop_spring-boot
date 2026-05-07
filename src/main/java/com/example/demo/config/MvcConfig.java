package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull; // Importación añadida
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) { // Etiqueta añadida
        // Obtenemos la ruta absoluta de una carpeta llamada "uploads" en la raíz del proyecto
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Le decimos a Spring que cuando alguien pida "/images/foto.jpg",
        // mire también en esa carpeta externa de nuestro disco duro.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/" + uploadPath + "/", "classpath:/static/images/");
    }
}