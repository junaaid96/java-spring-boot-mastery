package dev.junaaid96.jobs.web;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;

// DTOs as records: immutable, concise, and decoupled from the JPA entity.
public final class JobDtos {

    private JobDtos() {}

    // Incoming payload with Bean Validation rules; @Valid on the controller triggers them.
    public record JobRequest(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 2000) String description,
            @PositiveOrZero @Max(50) int experienceYears,
            @NotNull @Size(max = 20) List<@NotBlank String> skills) {}

    // Outgoing representation.
    public record JobResponse(Long id, String title, String description, int experienceYears,
                              List<String> skills, Instant createdAt) {}

    // Stable pagination envelope (don't leak Spring's Page JSON shape).
    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}

    // Login request/response for the demo token endpoint.
    public record TokenRequest(@NotBlank String username, @NotBlank String password) {}
    public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {}
}
