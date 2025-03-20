package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.example.model.Lemma;
import org.example.model.Page;
import org.example.model.Site;
import org.example.repository.LemmaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LemmaService {
    private final LemmaRepository lemmaRepository;

    public void processPage(Page page) {
        try {
            Map<String, Integer> lemmas = extractLemmas(page.getContent());
            Site site = page.getSite();

            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                Lemma lemma = lemmaRepository.findBySiteAndLemma(site, entry.getKey());
                if (lemma == null) {
                    lemma = new Lemma();
                    lemma.setSite(site);
                    lemma.setLemma(entry.getKey());
                    lemma.setFrequency(entry.getValue());
                } else {
                    lemma.setFrequency(lemma.getFrequency() + entry.getValue());
                }
                lemmaRepository.save(lemma);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке лемм страницы: " + page.getPath(), e);
        }
    }

    public Map<String, Integer> extractLemmas(String content) throws IOException {
        Map<String, Integer> lemmas = new HashMap<>();
        Analyzer analyzer = new RussianAnalyzer();
        org.apache.lucene.analysis.TokenStream stream = analyzer.tokenStream("", content);
        stream.reset();

        while (stream.incrementToken()) {
            String lemma = stream.getAttribute(CharTermAttribute.class).toString();
            lemmas.merge(lemma, 1, Integer::sum);
        }

        stream.end();
        stream.close();
        analyzer.close();

        return lemmas;
    }
} 