package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {

    Optional<News> findBySlug(String newsSlug);

    void deleteBySlug(String newsSlug);

    @Query("SELECT n FROM News n WHERE lower(n.title) LIKE :title")
    Page<News> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT COUNT(n) > 0 FROM News n WHERE n.slug = :newsSlug")
    boolean existsByNewsSlug(@Param("newsSlug") String newsSlug);
}