package org.example.dto;

import org.example.entity.Status;

import java.time.LocalDateTime;

public record DocumentRequestDto(
        Long id,
        String author,
        String title,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
