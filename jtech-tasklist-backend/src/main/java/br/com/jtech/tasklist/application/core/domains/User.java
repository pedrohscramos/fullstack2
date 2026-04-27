package br.com.jtech.tasklist.application.core.domains;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        String email,
        String passwordHash,
        Instant createdAt,
        Instant updatedAt
) {
}
