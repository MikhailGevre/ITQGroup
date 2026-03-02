package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.dto.DocumentBatchDto;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.example.service.DocumentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {
    private final DocumentService service;

    @PostMapping
    public DocumentRequestDto create(@Valid @RequestBody DocumentDto dto) {
        return service.create(dto);
    }

    @GetMapping("/{documentId}")
    public DocumentRequestDto getDocument(
            @Positive
            @PathVariable Long documentId) {
        return service.getDocument(documentId);
    }

    @PostMapping("/batch")
    public DocumentRequestDto getBatch(@RequestBody DocumentBatchDto batchDto) {

    }


}
