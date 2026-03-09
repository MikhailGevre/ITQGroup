package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentDto(
        @NotBlank(message = "Укажите поле Автор")
        String author,
        @NotBlank(message = "Укажите поле Название")
        String title
) {
}
