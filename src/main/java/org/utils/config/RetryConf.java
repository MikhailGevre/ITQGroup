package org.utils.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RetryConf {
    @Value("${spring.resilience4j.retry.max-attempts}")
    private int maxAttempts;
    @Value("${spring.resilience4j.retry.wait-second}")
    private int waiteSecondRetrySeconds;

    @Bean
    public Retry retry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofSeconds(waiteSecondRetrySeconds))
                .build();
        return Retry.of("retry", config);
    }
}
