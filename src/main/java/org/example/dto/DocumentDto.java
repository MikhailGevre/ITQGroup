package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record DocumentDto(
        @NotBlank(message = "Укажите поле Автор")
        @JsonProperty("author")
        String author,
        @NotBlank(message = "Укажите поле Название")
        @JsonProperty("title")
        String title
) {
}
