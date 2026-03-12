package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Configuración de la seguridad (El "Pase VIP" JWT del que hablamos antes)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("Bearer Token")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                // Información general de tu API
                .info(new Info()
                        .title("API REST de MyShop")
                        .description("Documentación interactiva de los endpoints de la tienda.")
                        .contact(new Contact()
                                .name("Tu Nombre") // ¡Pon el tuyo!
                                .email("contacto@myshop.com")
                                .url("https://myshop.com")
                        )
                        .version("1.0")
                );
    }
}