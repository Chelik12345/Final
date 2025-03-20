package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.SiteConfig;
import org.example.model.Site;
import org.example.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexingService {
    private final SiteRepository siteRepository;
    private final PageIndexingService pageIndexingService;
    private final SiteConfig siteConfig;

    public void startIndexing(List<Site> sites) {
        if (sites == null) {
            sites = new ArrayList<>();
            for (SiteConfig.Site configSite : siteConfig.getSites()) {
                Site site = new Site();
                site.setUrl(configSite.getUrl());
                site.setName(configSite.getName());
                sites.add(site);
            }
        }
        
        if (sites.isEmpty()) {
            throw new RuntimeException("Список сайтов пуст");
        }
        
        for (Site site : sites) {
            Site existingSite = siteRepository.findByUrl(site.getUrl());
            if (existingSite != null) {
                existingSite.setStatus(Site.Status.INDEXING);
                existingSite.setStatusTime(LocalDateTime.now());
                existingSite.setLastError(null);
                siteRepository.save(existingSite);
                pageIndexingService.indexSite(existingSite);
            } else {
                site.setStatus(Site.Status.INDEXING);
                site.setStatusTime(LocalDateTime.now());
                siteRepository.save(site);
                pageIndexingService.indexSite(site);
            }
        }
    }

    public void stopIndexing() {
        List<Site> indexingSites = siteRepository.findAll().stream()
                .filter(site -> site.getStatus() == Site.Status.INDEXING)
                .toList();
        
        if (indexingSites.isEmpty()) {
            throw new RuntimeException("Индексация не запущена");
        }
        
        for (Site site : indexingSites) {
            site.setStatus(Site.Status.FAILED);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError("Индексация остановлена пользователем");
            siteRepository.save(site);
        }
    }

    public void indexPage(String url) {
        if (url == null || url.isEmpty()) {
            throw new RuntimeException("URL страницы не указан");
        }

        // Находим подходящий сайт по URL
        Site site = siteRepository.findAll().stream()
                .filter(s -> url.startsWith(s.getUrl()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Данная страница находится за пределами сайтов, указанных в конфигурационном файле"));

        pageIndexingService.indexPage(url, site);
    }
} 