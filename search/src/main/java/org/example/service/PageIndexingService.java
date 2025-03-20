package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Page;
import org.example.model.Site;
import org.example.repository.PageRepository;
import org.example.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class PageIndexingService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaService lemmaService;
    private final SearchIndexService searchIndexService;

    public void indexSite(Site site) {
        try {
            Set<String> processedUrls = new HashSet<>();
            PageIndexingTask task = new PageIndexingTask(site.getUrl(), site, processedUrls, this);
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(task);
            pool.shutdown();

            site.setStatus(Site.Status.INDEXED);
            site.setStatusTime(LocalDateTime.now());
            siteRepository.save(site);
        } catch (Exception e) {
            site.setStatus(Site.Status.FAILED);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError(e.getMessage());
            siteRepository.save(site);
        }
    }

    public void indexPage(String url, Site site) {
        try {
            Page page = new Page();
            page.setSite(site);
            page.setPath(url);
            page.setCode(200); // Здесь должна быть реальная проверка кода ответа
            page.setContent(""); // Здесь должен быть реальный контент страницы
            page.setCreatedAt(LocalDateTime.now());
            page.setUpdatedAt(LocalDateTime.now());
            
            pageRepository.save(page);
            
            lemmaService.processPage(page);
            searchIndexService.indexPage(page);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при индексации страницы: " + url, e);
        }
    }
} 