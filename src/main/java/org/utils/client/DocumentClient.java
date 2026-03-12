package org.utils.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.utils.exception.FallbackException;

@Slf4j
@RequiredArgsConstructor
@Component
public class DocumentClient {
    private final DocumentFeignClient client;

    @Retryable(maxAttemptsExpression = "${retryable.document-client.max-attempts}",
            backoff = @Backoff(delayExpression = "${retryable.document-client.delay}"))
    @CircuitBreaker(name = "document-client", fallbackMethod = "fallback")
    public DocumentRequestDto create(DocumentDto dto) {
        return client.create(dto);
    }

    public DocumentRequestDto fallback(DocumentDto dto, Throwable ex) {
        log.error("Сработал fallback, dto {}, exception {}",dto.title(), ex.getMessage(), ex);
        throw new FallbackException("Сервис временно не доступен " + dto.title());
    }
}
