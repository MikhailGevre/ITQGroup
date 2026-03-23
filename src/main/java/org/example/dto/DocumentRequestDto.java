package org.example.dto;

import lombok.Builder;
import org.example.entity.Status;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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
