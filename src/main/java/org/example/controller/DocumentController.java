package org.example.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.dto.*;
import org.example.entity.Result;
import org.example.service.DocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("${spring.url.api-documents}")
public class DocumentController {
    private final DocumentService service;
    @Value("${controller.default-value.threads}")
    private int defaultThreads;
    @Value("${controller.default-value.attempts}")
    private int defaultAttempts;


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

    @GetMapping("/{status}/batch-status")
    public List<Long> getBatchByStatus(@PathVariable String status) {
        return service.getBatchByStatus(status);
    }

    @PutMapping("/submit")
    public EnumMap<Result, List<Long>> submit(@RequestBody @Valid DocumentSubmitApproveDto approveDto) {
        return service.submit(approveDto.documentIds());
    }

    @PutMapping("/approved")
    public EnumMap<Result, List<Long>> approve(@RequestBody @Valid DocumentSubmitApproveDto approveDto) {
        return service.approve(approveDto.documentIds());
    }

    @GetMapping
    public List<DocumentRequestDto> getDocuments(@ModelAttribute @Valid DocumentSearchDto dto) {
        return service.findDocuments(dto);
    }

    @PostMapping("/{documentId}/concurrency-test")
    public DocumentConcurrencyResultDto concurrencyTest(@PathVariable Long documentId,
                                                        @RequestParam(required = false) Integer threads,
                                                        @RequestParam(required = false) Integer attempts) {

        int t = Optional.ofNullable(threads).orElse(defaultThreads);
        int a = Optional.ofNullable(attempts).orElse(defaultAttempts);

        return service.concurrencyTest(documentId, t, a);
    }

}
