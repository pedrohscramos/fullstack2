package br.com.jtech.tasklist.adapters.input.protocols.tasks;

import jakarta.validation.constraints.NotBlank;

public record UpdateTaskRequest(
        @NotBlank String title,
        String description,
        boolean completed
) {
}
