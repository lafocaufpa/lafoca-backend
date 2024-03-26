package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Lafoca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LafocaRepository extends JpaRepository<Lafoca, Long> {

    @Query(value = "SELECT " +
            "(SELECT COUNT(student_id) FROM student) AS totalStudents, " +
            "(SELECT COUNT(project_id) FROM project) AS totalProjects, " +
            "(SELECT COUNT(tcc_id) FROM tcc) AS totalTccs, " +
            "(SELECT COUNT(article_id) FROM article) AS totalArticles",
            nativeQuery = true)
    CountResult getCounts();


    public interface CountResult {
        Integer getTotalStudents();
        Integer getTotalProjects();
        Integer getTotalTccs();
        Integer getTotalArticles();
    }
}

