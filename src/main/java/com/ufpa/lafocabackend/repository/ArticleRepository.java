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

    @Query("SELECT a FROM Article a WHERE a.date = :year")
    Page<Article> findByDate(@Param("year") String year, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.linesOfResearch lor WHERE a.title LIKE %:title% AND lor.lineOfResearchId = :lineOfResearchId")
    Page<Article> findByTitleContainingAndLineOfResearchId(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.title LIKE %:title% AND a.date = :year")
    Page<Article> findByTitleContainingAndDate(@Param("title") String title, @Param("year") String year, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.linesOfResearch lor WHERE lor.lineOfResearchId = :lineOfResearchId AND a.date = :year")
    Page<Article> findByLineOfResearchIdAndDate(@Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

    @Query("SELECT a FROM Article a JOIN a.linesOfResearch lor WHERE a.title LIKE %:title% AND lor.lineOfResearchId = :lineOfResearchId AND a.date = :year")
    Page<Article> findByTitleContainingAndLineOfResearchIdAndDate(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

}

