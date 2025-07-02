package com.example.newsapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityScheme;        // ⬅ аннотация
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(                        // декларация схемы (у вас уже есть)
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(                     // ⬅ глобальное требование
        security = @SecurityRequirement(name = "bearerAuth"))
public class OpenApiConfig {

    @Bean
    public OpenAPI newsAppOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("NewsApp API").version("v1.0"));
                // только требование! саму схему уже добавила аннотация

    }
}

