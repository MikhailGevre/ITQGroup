package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record DocumentBatchDto(
        @NotEmpty
        List<Long> ids,

        @Min(0)
        Integer page,

        @Positive
        Integer size,
        String sortBy,
        String direction
) {
    public DocumentBatchDto {
        page = page == null ? 0 : page;
        size = size == null ? 10 : size;
        sortBy = sortBy == null ? "createdAt" : sortBy;
        direction = direction == null ? "DESC" : direction;
    }
}
