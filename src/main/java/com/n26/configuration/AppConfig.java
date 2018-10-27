package com.n26.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Async
public class AppConfig {

    @Value("${threadpool.corepoolsize:50}")
    int corePoolSize;
    @Value("${threadpool.maxpoolsize:300}")
    int maxPoolSize;
    @Value("${transaction.lifetime.in.seconds:60}")
    private long lifeTimeInSeconds;

    public long getLifeTimeInSeconds(){
        return this.lifeTimeInSeconds;
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setThreadNamePrefix("n26-statistics-lifetime-counter-");
        return pool;
    }

}
