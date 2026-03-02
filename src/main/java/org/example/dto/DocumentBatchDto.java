package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

public record DocumentBatchDto(
        @NotEmpty
        List<Long> ids,
        @Min(0)
        int page,
        @Positive
        int size,
        String sortBy,
        String direction
) {
}
