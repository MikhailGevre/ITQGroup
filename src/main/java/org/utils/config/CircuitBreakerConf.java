package org.utils.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Configuration
public class CircuitBreakerConf {
    @Value("${spring.resilience4j.circuit-breaker.rate-threshold}")
    private int rateThreshold;
    @Value("${spring.resilience4j.circuit-breaker.wait-second}")
    private int waitSecondCircuitSeconds;

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(rateThreshold)
                .waitDurationInOpenState(Duration.ofSeconds(waitSecondCircuitSeconds))
                .build();
        return CircuitBreaker.of("circuitBreaker", config);
    }

    private Mono<ResponseEntity<String>> fallback(String uri, Throwable ex) {
        log.warn("Fallback сработал для {}", uri, ex);
        return Mono.just(ResponseEntity.status(503).body("Сервис временно недоступен"));
    }
}
