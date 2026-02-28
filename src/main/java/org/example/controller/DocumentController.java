package org.example.controller;

import lombok.RequiredArgsConstructor;
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
    public void create() {

    }

}
