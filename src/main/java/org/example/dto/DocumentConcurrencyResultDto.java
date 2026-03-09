package org.example.dto;

import org.example.entity.Status;

public record DocumentConcurrencyResultDto(
        int successful,
        int failed,
        Status status
) {
}
