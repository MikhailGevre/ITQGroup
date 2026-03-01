package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.example.entity.Document;
import org.example.entity.Status;
import org.example.mapper.DocumentMapper;
import org.example.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentMapper mapper;

    @Transactional
    public DocumentRequestDto create(DocumentDto dto) {
        Document document = mapper.toEntity(dto);
        document.setStatus(Status.DRAFT);
        repository.save(document);
        return mapper.toDto(document);
    }
}
