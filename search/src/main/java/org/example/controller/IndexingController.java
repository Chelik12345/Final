package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Site;
import org.example.service.IndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class IndexingController {
    private final IndexingService indexingService;

    @GetMapping("/api/startIndexing")
    public ResponseEntity<Map<String, Object>> startIndexing() {
        try {
            indexingService.startIndexing(null);
            return ResponseEntity.ok(Map.of("result", true));
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/api/stopIndexing")
    public ResponseEntity<Map<String, Object>> stopIndexing() {
        try {
            indexingService.stopIndexing();
            return ResponseEntity.ok(Map.of("result", true));
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/api/indexPage")
    public ResponseEntity<Map<String, Object>> indexPage(@RequestParam String url) {
        try {
            indexingService.indexPage(url);
            return ResponseEntity.ok(Map.of("result", true));
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 