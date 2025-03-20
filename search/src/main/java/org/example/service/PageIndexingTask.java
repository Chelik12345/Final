package org.example.service;

import org.example.model.Site;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public class PageIndexingTask extends RecursiveAction {
    private final String url;
    private final Site site;
    private final Set<String> processedUrls;
    private final PageIndexingService pageIndexingService;

    public PageIndexingTask(String url, Site site, Set<String> processedUrls, PageIndexingService pageIndexingService) {
        this.url = url;
        this.site = site;
        this.processedUrls = processedUrls;
        this.pageIndexingService = pageIndexingService;
    }

    @Override
    protected void compute() {
        if (processedUrls.contains(url)) {
            return;
        }
        processedUrls.add(url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; SearchBot/1.0; +http://www.example.com)")
                    .get();

            pageIndexingService.indexPage(url, site);

            Elements links = doc.select("a[href]");
            List<PageIndexingTask> tasks = new ArrayList<>();

            for (Element link : links) {
                String href = link.absUrl("href");
                if (href.startsWith(site.getUrl()) && !processedUrls.contains(href)) {
                    tasks.add(new PageIndexingTask(href, site, processedUrls, pageIndexingService));
                }
            }

            invokeAll(tasks);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке страницы: " + url, e);
        }
    }
} 