package org.example.dto;

import java.util.List;

public record DocumentApproveDto(
        List<Long> documentIds
) {
}
