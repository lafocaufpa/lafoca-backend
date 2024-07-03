package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);

    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title%")
    Page<Article> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.linesOfResearch lor WHERE lor.lineOfResearchId = :lineOfResearchId")
    Page<Article> findByLineOfResearchId(@Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);
}
