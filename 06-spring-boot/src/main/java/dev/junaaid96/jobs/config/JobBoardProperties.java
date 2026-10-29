package dev.junaaid96.jobs.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// Type-safe, validated configuration bound from "jobboard.*" in application.yml. Fails fast at startup if invalid.
@Validated
@ConfigurationProperties(prefix = "jobboard")
public record JobBoardProperties(
        // HMAC secret for signing JWTs (>= 32 chars for HS256). Override via env var JOBBOARD_JWT_SECRET.
        @NotBlank @Size(min = 32) String jwtSecret,
        // Token lifetime in minutes.
        @Min(1) @Max(1440) int tokenTtlMinutes,
        // Maximum page size clients may request.
        @Min(1) @Max(500) int maxPageSize) {}
