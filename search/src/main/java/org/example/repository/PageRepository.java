package org.example.repository;

import org.example.model.Page;
import org.example.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {
    List<Page> findBySite(Site site);
    Page findBySiteAndPath(Site site, String path);
} 