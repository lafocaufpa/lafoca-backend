package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Lafoca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LafocaRepository extends JpaRepository<Lafoca, Long> {

    @Query(value = "SELECT " +
            "(SELECT COUNT(member_id) FROM member) AS totalMembers, " +
            "(SELECT COUNT(project_id) FROM project) AS totalProjects, " +
            "(SELECT COUNT(tcc_id) FROM tcc) AS totalTccs, " +
            "(SELECT COUNT(article_id) FROM article) AS totalArticles",
            nativeQuery = true)
    CountResult getCounts();


    interface CountResult {
        Integer getTotalMembers();
        Integer getTotalProjects();
        Integer getTotalTccs();
        Integer getTotalArticles();
    }

    Optional<Lafoca> findFirstByOrderByDateTimeDesc();
}

