package org.example.repository;

import org.example.model.Lemma;
import org.example.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    List<Lemma> findBySite(Site site);
    Lemma findBySiteAndLemma(Site site, String lemma);
    
    @Query("SELECT l FROM Lemma l WHERE l.lemma IN :lemmas AND l.site = :site")
    List<Lemma> findByLemmasAndSite(List<String> lemmas, Site site);
} 