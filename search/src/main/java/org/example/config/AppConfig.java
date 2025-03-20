package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.example.service.PageIndexingTask;
import org.example.service.PageIndexingService;

@Configuration
public class AppConfig {
    @Bean
    @Scope("prototype")
    public PageIndexingTask pageIndexingTask(PageIndexingService pageIndexingService) {
        return new PageIndexingTask(null, null, null, pageIndexingService);
    }
} 