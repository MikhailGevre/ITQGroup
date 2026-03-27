package org.utils.generator;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.utils.client.DocumentClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class GenerateDocuments {
    private final DocumentClient documentClient;
    private final Executor executor;
    @Value("${spring.url.api-documents}")
    private String endpoint;
    @Value("${generator.document-create}")
    private int countDocument;

    public GenerateDocuments(DocumentClient documentClient,
                             @Qualifier(value = "customExecutor") Executor executor) {
        this.documentClient = documentClient;
        this.executor = executor;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("Запуск генерации {} документов", countDocument);
        generateDocuments();
    }

    public void generateDocuments() {
        List<CompletableFuture<ResponseEntity<String>>> futures = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < countDocument; i++) {
            int numberDocument = counter.incrementAndGet();
            CompletableFuture<ResponseEntity<String>> future = CompletableFuture.supplyAsync(() -> {
                DocumentDto dto =
                        new DocumentDto("Автор № " + numberDocument, "Название № " + numberDocument);

                ResponseEntity<String> response = documentClient.exchange(HttpMethod.POST, endpoint, dto);

                if (response.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                    log.error("Ошибка при генерации документов, номер генерации {}", numberDocument);
                    counter.decrementAndGet();
                    return null;
                }
                log.info("Успешная генерация документа {}", dto);
                return response;
            }, executor);
            futures.add(future);
        }

        long successCount = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .count();

        log.info("Генерация документов завершена. Успешно: {}/{}", successCount, countDocument);
    }
}
