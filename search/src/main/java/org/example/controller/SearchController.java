package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Site;
import org.example.service.IndexingService;
import org.example.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final IndexingService indexingService;
    private final SearchService searchService;

    @PostMapping("/api/startIndexing")
    public ResponseEntity<Map<String, Boolean>> startIndexing(@RequestBody List<Site> sites) {
        indexingService.startIndexing(sites);
        return ResponseEntity.ok(Map.of("result", true));
    }

    @PostMapping("/api/stopIndexing")
    public ResponseEntity<Map<String, Boolean>> stopIndexing() {
        indexingService.stopIndexing();
        return ResponseEntity.ok(Map.of("result", true));
    }

    @GetMapping("/api/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String query,
            @RequestParam(required = false) String site) {
        List<SearchService.SearchResult> results = searchService.search(query, site);
        return ResponseEntity.ok(results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("site", result.getPage().getSite().getUrl());
                    map.put("siteName", result.getPage().getSite().getName());
                    map.put("uri", result.getPage().getPath());
                    map.put("title", extractTitle(result.getPage().getContent()));
                    map.put("snippet", generateSnippet(result.getPage().getContent(), query));
                    map.put("relevance", result.getRelevance());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private String extractTitle(String content) {
        // Здесь должна быть реализация извлечения заголовка из HTML
        return "Заголовок страницы";
    }

    private String generateSnippet(String content, String query) {
        // Здесь должна быть реализация генерации сниппета
        return "Сниппет страницы";
    }
} 