package br.com.jtech.tasklist.adapters.input.protocols.tasks;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
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
