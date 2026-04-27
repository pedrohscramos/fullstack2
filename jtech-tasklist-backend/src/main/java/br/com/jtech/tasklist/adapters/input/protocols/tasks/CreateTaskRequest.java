package br.com.jtech.tasklist.adapters.input.protocols.tasks;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTaskRequest(
        @NotNull UUID listId,
        @NotBlank String title,
        String description,
        boolean completed
) {
}
