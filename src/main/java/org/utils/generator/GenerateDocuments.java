package org.utils.generator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DocumentDto;
import org.example.dto.DocumentRequestDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.utils.client.DocumentClient;
import org.utils.exception.FallbackException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class GenerateDocuments {
    @Value("${generator.document-create}")
    private int countDocument;
    private final DocumentClient documentClient;
    private final Executor executor;

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
        List<CompletableFuture<DocumentRequestDto>> futures = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < countDocument; i++) {
            CompletableFuture<DocumentRequestDto> future = CompletableFuture.supplyAsync(() -> {
                DocumentDto dto =
                        new DocumentDto("Автор № " + counter, "Название № " + counter);
                try {
                    counter.incrementAndGet();
                    return documentClient.create(dto);
                } catch (FallbackException e) {
                    log.error("Ошибка при генерации документов, номер генерации {}, исключение: ", counter.get(), e);
                    return null;
                }
            }, executor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long successCount = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .count();

        log.info("Генерация документов завершена. Успешно: {}/{}", successCount, countDocument);
    }
}
