package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DocumentBatchDto;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.example.entity.Document;
import org.example.entity.Status;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.DocumentMapper;
import org.example.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentMapper mapper;

    @Transactional
    public DocumentRequestDto create(DocumentDto dto) {
        Document document = mapper.toEntity(dto);
        document.setStatus(Status.DRAFT);
        Document saved = repository.save(document);
        return mapper.toDto(saved);
    }

    public DocumentRequestDto getDocument(Long documentId) {
        Document document = repository.findById(documentId).orElseThrow(()->
                new EntityNotFoundException("Документ не найдет id: " + documentId));
        return mapper.toDto(document);
    }

    public DocumentRequestDto getBatch(DocumentBatchDto batchDto) {

    }
}
