package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Lemma;
import org.example.model.Page;
import org.example.model.SearchIndex;
import org.example.repository.LemmaRepository;
import org.example.repository.SearchIndexRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchIndexService {
    private final LemmaRepository lemmaRepository;
    private final SearchIndexRepository searchIndexRepository;
    private final LemmaService lemmaService;

    public void indexPage(Page page) {
        try {
            Map<String, Integer> lemmas = lemmaService.extractLemmas(page.getContent());
            List<Lemma> pageLemmas = lemmaRepository.findByLemmasAndSite(
                    lemmas.keySet().stream().collect(Collectors.toList()),
                    page.getSite()
            );

            for (Lemma lemma : pageLemmas) {
                SearchIndex searchIndex = new SearchIndex();
                searchIndex.setPage(page);
                searchIndex.setLemma(lemma);
                searchIndex.setRank(calculateRank(lemma, lemmas.get(lemma.getLemma())));
                searchIndexRepository.save(searchIndex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании поискового индекса для страницы: " + page.getPath(), e);
        }
    }

    private float calculateRank(Lemma lemma, Integer frequency) {
        return (float) frequency / lemma.getFrequency();
    }
} 