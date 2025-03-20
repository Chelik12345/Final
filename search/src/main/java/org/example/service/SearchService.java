package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Lemma;
import org.example.model.Page;
import org.example.model.SearchIndex;
import org.example.model.Site;
import org.example.repository.LemmaRepository;
import org.example.repository.SearchIndexRepository;
import org.example.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final LemmaRepository lemmaRepository;
    private final SearchIndexRepository searchIndexRepository;
    private final SiteRepository siteRepository;
    private final LemmaService lemmaService;

    public List<SearchResult> search(String query, String siteUrl) {
        try {
            List<String> queryLemmas = lemmaService.extractLemmas(query).keySet().stream()
                    .collect(Collectors.toList());

            Site site = siteUrl != null ? siteRepository.findByUrl(siteUrl) : null;
            List<Lemma> lemmas = site != null ?
                    lemmaRepository.findByLemmasAndSite(queryLemmas, site) :
                    lemmaRepository.findAll().stream()
                            .filter(l -> queryLemmas.contains(l.getLemma()))
                            .collect(Collectors.toList());

            if (lemmas.isEmpty()) {
                return Collections.emptyList();
            }

            Map<Page, Float> pageRanks = new HashMap<>();
            for (Lemma lemma : lemmas) {
                List<SearchIndex> indexes = searchIndexRepository.findByLemma(lemma);
                for (SearchIndex index : indexes) {
                    pageRanks.merge(index.getPage(), index.getRank(), Float::sum);
                }
            }

            return pageRanks.entrySet().stream()
                    .sorted(Map.Entry.<Page, Float>comparingByValue().reversed())
                    .map(entry -> new SearchResult(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при выполнении поиска", e);
        }
    }

    public static class SearchResult {
        private final Page page;
        private final float relevance;

        public SearchResult(Page page, float relevance) {
            this.page = page;
            this.relevance = relevance;
        }

        public Page getPage() {
            return page;
        }

        public float getRelevance() {
            return relevance;
        }
    }
} 