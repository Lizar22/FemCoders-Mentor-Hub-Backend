package com.fcmh.femcodersmentorhub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI femCodersMentorHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("FemCoders Mentor Hub API")
                        .description("REST API built with Java/Spring Boot that connects female mentors and mentees in technology through mentoring sessions. It implements JWT authentication, advanced search filters, and layered architecture with JPA/Hibernate.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FemCoders Mentor Hub Team")
                                        .email("hello@femcodersmentorhub.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        . description("Enter JWT token")));
    }
}