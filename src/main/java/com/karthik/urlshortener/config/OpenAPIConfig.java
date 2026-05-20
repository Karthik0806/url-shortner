package com.karthik.urlshortener.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName =
                "bearerAuth";

        return new OpenAPI()

                .info(
                        new Info()

                                .title(
                                        "KShort API"
                                )

                                .version("1.0.0")

                                .description(
                                        """
                                        Production-grade scalable URL shortener API
                                        built with Spring Boot, PostgreSQL, Redis,
                                        JWT authentication, analytics, and rate limiting.
                                        """
                                )

                                .contact(
                                        new Contact()

                                                .name(
                                                        "Karthik Narravula"
                                                )

                                                .url(
                                                        "https://karthiknarravula.dev"
                                                )
                                )
                )

                .servers(
                        List.of(

                                new Server()

                                        .url(
                                                "http://localhost:8080"
                                        )

                                        .description(
                                                "Local Development"
                                        ),

                                new Server()

                                        .url(
                                                "https://shortner.karthiknarravula.dev"
                                        )

                                        .description(
                                                "Production"
                                        )
                        )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(
                                        securitySchemeName
                                )
                )

                .components(
                        new Components()

                                .addSecuritySchemes(
                                        securitySchemeName,

                                        new SecurityScheme()

                                                .name(
                                                        securitySchemeName
                                                )

                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )

                                                .scheme("bearer")

                                                .bearerFormat(
                                                        "JWT"
                                                )
                                )
                );
    }
}