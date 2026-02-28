package org.example.dto;

public record DocumentDto(
        @NotBlank
        String author,
        String title
) {
}
