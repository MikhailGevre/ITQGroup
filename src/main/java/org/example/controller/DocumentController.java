package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.entity.Result;
import org.example.service.DocumentService;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.EnumMap;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {
    private final DocumentService service;

    @ResponseStatus(HttpStatus.CREATED)
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
    public List<DocumentRequestDto> getBatch(@RequestBody @Valid DocumentBatchDto batchDto) {
        return service.getBatch(batchDto);
    }

    @PutMapping("/submit-for-approval")
    public EnumMap<Result, List<Long>> sendToApprove(@RequestBody @Valid DocumentApproveDto approveDto) {
        return service.sendToApprove(approveDto.documentIds());
    }

    @PutMapping("/approved")
    public EnumMap<Result, List<Long>> approve(@RequestBody @Valid DocumentApproveDto approveDto) {
        return service.approve(approveDto.documentIds());
    }

    @GetMapping
    public List<DocumentRequestDto> getDocuments(@ModelAttribute @Valid DocumentSearchDto dto) {
        return service.findDocuments(dto);
    }

    @PostMapping("/{documentId}/concurrency-test")
    public DocumentConcurrencyResultDto concurrencyTest(@PathVariable Long documentId,
                                @RequestParam @DefaultValue(value = "5") int threads,
                                @RequestParam @DefaultValue(value = "3")int attempts) {

        return service.concurrencyTest(documentId, threads, attempts);
    }

}
