package org.example.repository;

import org.example.model.Lemma;
import org.example.model.Page;
import org.example.model.SearchIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchIndexRepository extends JpaRepository<SearchIndex, Integer> {
    List<SearchIndex> findByPage(Page page);
    List<SearchIndex> findByLemma(Lemma lemma);
    
    @Query("SELECT si FROM SearchIndex si WHERE si.lemma IN :lemmas AND si.page = :page")
    List<SearchIndex> findByLemmasAndPage(List<Lemma> lemmas, Page page);
} 