package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.example.service.DocumentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/documents")
public class DocumentController {
    private final DocumentService service;

    @PostMapping
    public DocumentRequestDto create(DocumentDto dto) {
        return service.create(dto);

    }


}
