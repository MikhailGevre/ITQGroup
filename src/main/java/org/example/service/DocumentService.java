package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.entity.Document;
import org.example.entity.Result;
import org.example.entity.Status;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.DocumentMapper;
import org.example.repository.DocumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentMapper mapper;
    private final RegisterService registerService;

    @Transactional
    public DocumentRequestDto create(DocumentDto dto) {
        Document document = mapper.toEntity(dto);
        document.setStatus(Status.DRAFT);
        Document saved = repository.save(document);

        return mapper.toDto(saved);
    }

    public DocumentRequestDto getDocument(Long documentId) {
        Document document = getDocumentOrThrow(documentId);
        return mapper.toDto(document);
    }

    public List<DocumentRequestDto> getBatch(DocumentBatchDto batchDto) {
        int size = batchDto.size();
        int page = batchDto.page();
        String sortBy = batchDto.sortBy();
        String direction = batchDto.direction();
        Pageable pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        List<Document> documents = repository.findAllByIdsByPageable(batchDto.ids(), pageRequest);

        return documents.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public EnumMap<Result, List<Long>> sendToApprove(Long[] documentIds) {

        List<DocumentUpdateRow> updateRows = repository.sendToApprove(documentIds);

        return updateRows.stream()
                .collect(Collectors.groupingBy(row -> Result.valueOf(row.getResult()),
                        () -> new EnumMap<>(Result.class),
                        Collectors.mapping(DocumentUpdateRow::getId, Collectors.toList())
                ));
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public EnumMap<Result, List<Long>> approve(Long[] documentIds) {
        List<DocumentUpdateRow> updateRows = repository.checkCandidates(documentIds);
        EnumMap<Result, List<Long>> candidates = updateRows.stream()
                .collect(Collectors.groupingBy(row ->
                                Result.valueOf(row.getResult()),
                        () -> new EnumMap<>(Result.class),
                        Collectors.mapping(DocumentUpdateRow::getId, Collectors.toList())
                ));
        if (candidates.get(Result.CANDIDATE) == null) {
            return candidates;
        }
        List<Long> registersDocument = approveCandidate(candidates.get(Result.CANDIDATE).toArray(Long[]::new));
        candidates.remove(Result.CANDIDATE);
        candidates.put(Result.APPROVED, registersDocument);

        return candidates;
    }

    public List<Long> approveCandidate(Long[] documentIds) {
        Long[] approvedIds = repository.approveCandidates(documentIds);

        return registerService.registerDocument(approvedIds);
    }

    @Transactional
    public List<DocumentRequestDto> findDocuments(DocumentSearchDto dto) {
        LocalDateTime createdFrom = dto.createdFrom().atStartOfDay();
        LocalDateTime createdTo = dto.createdTo().atTime(LocalTime.MAX);
        List<Document> documents =
                repository.getDocuments(dto.status(), dto.author(), createdFrom, createdTo);

        return documents.stream()
                .map(mapper::toDto)
                .toList();
    }

    public DocumentConcurrencyResultDto concurrencyTest(Long documentId, int threads, int attempts) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successful = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Document document = getDocumentOrThrow(documentId);

        for (int i = 0; i < attempts; i++) {
            CompletableFuture<Void> future =
                    CompletableFuture.runAsync(() -> {
                                repository.approveDocument(document);
                                successful.incrementAndGet();
                            }, executor)
                            .exceptionally(ex -> {
                                failed.incrementAndGet();
                                return null;
                            });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        executor.shutdown();

        return new DocumentConcurrencyResultDto(successful.get(), failed.get(), document.getStatus());
    }


    private Document getDocumentOrThrow(long documentId) {
        return repository.findById(documentId).orElseThrow(() ->
                new EntityNotFoundException("Документ не найдет id: " + documentId));
    }
}

