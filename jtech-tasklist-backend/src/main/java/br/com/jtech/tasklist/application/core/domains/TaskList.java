package br.com.jtech.tasklist.application.core.domains;

import java.time.Instant;
import java.util.UUID;

public record TaskList(
        UUID id,
        UUID userId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
