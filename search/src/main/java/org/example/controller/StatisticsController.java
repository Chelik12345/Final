package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.repository.PageRepository;
import org.example.repository.SiteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatisticsController {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;

    @GetMapping("/api/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        Map<String, Object> total = new HashMap<>();
        
        total.put("sites", siteRepository.count());
        total.put("pages", pageRepository.count());
        total.put("lemmas", 0); // Можно добавить подсчет лемм
        total.put("isIndexing", false);
        
        statistics.put("total", total);
        statistics.put("detailed", new ArrayList<>());
        
        return ResponseEntity.ok(statistics);
    }
} 