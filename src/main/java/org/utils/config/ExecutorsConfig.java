package org.utils.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ExecutorsConfig {
    @Value("${executors.min-thread}")
    private int minThread;
    @Value("${executors.max-thread}")
    private int maxThread;
    @Value("${executors.queue-task}")
    private int queueTask;
    @Value("${executors.thread-name}")
    private String threadName;
    @Value("${executors.wait-for-task}")
    private boolean waitForTask;
    @Value("${executors.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Bean("customExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(minThread);
        executor.setMaxPoolSize(maxThread);
        executor.setQueueCapacity(queueTask);
        executor.setThreadNamePrefix(threadName);
        executor.setWaitForTasksToCompleteOnShutdown(waitForTask);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}
