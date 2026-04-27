package br.com.jtech.tasklist.adapters.input.protocols.lists;

import java.time.Instant;
import java.util.UUID;

public record TasklistResponse(
        UUID id,
        UUID userId,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
