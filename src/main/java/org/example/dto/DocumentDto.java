package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentDto(
        @NotBlank
        String author,
        @NotBlank
        String title
) {
}
