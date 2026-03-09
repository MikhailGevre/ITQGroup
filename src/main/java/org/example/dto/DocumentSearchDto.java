package org.example.dto;

import jakarta.validation.constraints.NotNull;
import org.example.entity.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record DocumentSearchDto(
        Status status,
        String author,
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        @NotNull(message = "Укажите дату поиска от")
        LocalDateTime createdFrom,
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        @NotNull(message = "Укажите дату поиска до")
        LocalDateTime createdTo
) {
}
