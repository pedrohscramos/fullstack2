package br.com.jtech.tasklist.adapters.input.protocols.lists;

import jakarta.validation.constraints.NotBlank;

public record UpdateTasklistRequest(
        @NotBlank String name
) {
}
