package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Optional<News> findBySlug(String newsSlug);
}
