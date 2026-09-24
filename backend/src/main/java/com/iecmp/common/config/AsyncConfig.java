package com.iecmp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池：用于操作日志落库、站内消息推送、提交数据采集等旁路任务。
 *
 * <p>拒绝策略选择 {@code CallerRunsPolicy}，队列满时由调用线程执行，避免任务静默丢失。
 */
@Configuration
public class AsyncConfig {

    /** 通用异步执行器。 */
    @Bean("iecmpAsyncExecutor")
    public Executor iecmpAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("iecmp-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
