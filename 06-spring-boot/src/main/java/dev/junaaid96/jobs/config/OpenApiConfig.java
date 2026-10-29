package dev.junaaid96.jobs.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Document metadata and the "bearer JWT" scheme so Swagger UI shows an Authorize button.
@Configuration
@OpenAPIDefinition(info = @Info(title = "Job Board API", version = "v1", description = "Learning project for Spring Boot 4"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {}
