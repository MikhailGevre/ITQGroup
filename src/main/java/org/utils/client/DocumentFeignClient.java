package org.utils.client;

import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.springframework.web.bind.annotation.PostMapping;

@org.springframework.cloud.openfeign.FeignClient(name = "documentFeign", url = "${feign.document-feign}")
public interface DocumentFeignClient {

    @PostMapping
    DocumentRequestDto create(DocumentDto dto);
}
