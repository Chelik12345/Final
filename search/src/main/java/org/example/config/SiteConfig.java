package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "indexing-settings")
public class SiteConfig {
    private List<Site> sites;

    @Data
    public static class Site {
        private String url;
        private String name;
    }
} 