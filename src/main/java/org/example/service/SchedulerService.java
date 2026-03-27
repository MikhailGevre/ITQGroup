package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DocumentSubmitApproveDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.utils.client.DocumentClient;
import org.utils.exception.JsonParseException;

import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Component
public class SchedulerService {
    private final DocumentClient documentClient;
    private final ObjectMapper objectMapper;
    @Value("${spring.url.api-documents}")
    private String resourceDocuments;
    @Value("${scheduled.submit.name}")
    private String workerSubmitName;
    @Value("${scheduled.approve.name}")
    private String workerApproveName;
    private final ReentrantLock submitLock = new ReentrantLock();
    private final ReentrantLock approveLock = new ReentrantLock();


    @Scheduled(cron = "${scheduled.submit.cron}")
    public void workerSubmit() {
        if (!submitLock.tryLock()) {
            concurrencyLog(workerSubmitName);
            return;
        }
        try {
            String endpointGet = resourceDocuments + "/DRAFT" + "/batch-status";
            String endpointPut = resourceDocuments + "/submit";

            submitDocumentsForApproval(endpointGet, HttpMethod.GET, endpointPut, HttpMethod.PUT, workerSubmitName);
        } finally {
            submitLock.unlock();
        }

    }

    @Scheduled(cron = "${scheduled.approve.cron}")
    public void workerApprove() {
        if (!approveLock.tryLock()) {
            concurrencyLog(workerApproveName);
            return;
        }
        try {
            String endpointGet = resourceDocuments + "/SUBMITTED" + "/batch-status";
            String endpointPut = resourceDocuments + "/approved";

            submitDocumentsForApproval(endpointGet, HttpMethod.GET, endpointPut, HttpMethod.PUT, workerApproveName);
        } finally {
            approveLock.unlock();
        }
    }

    private void submitDocumentsForApproval(String endpointGet, HttpMethod httpMethodGet,
                                            String endpointPut, HttpMethod httpMethodPut,
                                            String workerName) {
        log.debug("{} начал свою работу", workerName);
        ResponseEntity<String> raw = documentClient.exchange(httpMethodGet, endpointGet, null);

        if (raw.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
            log.warn("Ошибка сервиса, статус код {}", raw.getStatusCode().value());
            return;
        }

        Long[] documentsIds;

        try {
            documentsIds = objectMapper.readValue(raw.getBody(), Long[].class);
            log.info("Успешное получение пачки документов - {}, по ендпоинту {}", documentsIds.length, endpointGet);
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка парсинга Json {}", raw.getBody());
            throw new JsonParseException(e.getMessage());
        }

        DocumentSubmitApproveDto dto = new DocumentSubmitApproveDto(documentsIds);
        ResponseEntity<String> request = documentClient.exchange(httpMethodPut, endpointPut, dto);

        if (request.getStatusCode().value() == HttpStatus.OK.value()) {
            log.info("{} - успешная обработка документов", workerName);
        } else {
            log.warn("{} произошла ошибка при обработке документов. Статус код {}, ресурс {}, метод {}", workerName,
                    request.getStatusCode().value(), endpointPut, httpMethodPut);
        }
    }

    private void concurrencyLog(String workerName) {
        log.debug("{} уже выполняется, пропуск", workerName);
    }
}
