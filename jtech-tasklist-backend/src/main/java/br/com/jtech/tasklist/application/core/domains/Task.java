package br.com.jtech.tasklist.application.core.domains;

import java.time.Instant;
import java.util.UUID;

public record Task(
        UUID id,
        UUID userId,
        UUID listId,
        String title,
        String description,
        boolean completed,
        Instant completedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
