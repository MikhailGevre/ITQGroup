package org.example.dto;

import org.example.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentRequestDto(
        Long id,
        String author,
        String title,
        Status status,
        List<HistoryDto> histories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
