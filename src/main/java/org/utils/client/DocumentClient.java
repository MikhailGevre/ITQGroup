package org.utils.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.utils.config.CircuitBreakerConf;
import org.utils.config.RestTemplateConf;
import org.utils.config.RetryConf;
import org.utils.exception.ServiceRequestError;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component
public class DocumentClient {
    private final RestTemplateConf restTemplateConf;
    private final CircuitBreakerConf circuitBreakerConf;
    private final RetryConf retryConf;
    @Value("${spring.url.base-documents-api}")
    private String basePath;

    public ResponseEntity<String> exchange(HttpMethod httpMethod, String endpoint, Object body) {
        RestTemplate restTemplate = restTemplateConf.webClient();
        CircuitBreaker circuitBreaker = circuitBreakerConf.circuitBreaker();
        Retry retry = retryConf.retry();
        String path = basePath + endpoint;

        Supplier<ResponseEntity<String>> supplier = () -> {
            try {
                HttpEntity<Object> request = new HttpEntity<>(body);
                return restTemplate.exchange(path, httpMethod, request, String.class);
            } catch (RestClientException ex) {
                log.error("Ошибка при вызове {}: {}", endpoint, ex.getMessage());
                throw new ServiceRequestError(ex.getMessage());
            }
        };

        supplier = CircuitBreaker.decorateSupplier(circuitBreaker, supplier);
        supplier = Retry.decorateSupplier(retry, supplier);

        try {
            return supplier.get();
        } catch (CallNotPermittedException e) {
            log.warn("Fallback из-за CircuitBreaker открыт для {}", endpoint, e);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Сервис недоступен: CircuitBreaker открыт");
        } catch (RestClientException e) {
            log.warn("Fallback из-за ошибки HTTP при вызове {}", endpoint, e);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Сервис недоступен: ошибка HTTP");
        } catch (Exception e) {
            log.warn("Fallback после всех попыток Retry для {}", endpoint, e);

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Сервис недоступен: Retry исчерпан");
        }
    }
}
