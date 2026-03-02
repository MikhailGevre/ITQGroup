package org.example.dto;

import org.example.entity.Action;

import java.time.LocalDateTime;

public record HistoryDto(
        Long id,
        String author,
        String comment,
        Action action,
        Long documentId,
        LocalDateTime createdAt
) {
}
