package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findBySlug(String slug);

    @Query("SELECT p FROM Project p WHERE p.title LIKE %:title%")
    Page<Project> findByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE l.lineOfResearchId = :lineOfResearchId")
    Page<Project> findByLineOfResearchId(@Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.date = :year")
    Page<Project> findByDate(@Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE p.title LIKE %:title% AND l.lineOfResearchId = :lineOfResearchId")
    Page<Project> findByTitleContainingAndLineOfResearchId(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.title LIKE %:title% AND p.date = :year")
    Page<Project> findByTitleContainingAndDate(@Param("title") String title, @Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE l.lineOfResearchId = :lineOfResearchId AND p.date = :year")
    Page<Project> findByLineOfResearchIdAndDate(@Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE p.title LIKE %:title% AND l.lineOfResearchId = :lineOfResearchId AND p.date = :year")
    Page<Project> findByTitleContainingAndLineOfResearchIdAndDate(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

    // Novas consultas para projetos em andamento

    @Query("SELECT p FROM Project p WHERE p.title LIKE %:title% AND p.endDate IS NULL")
    Page<Project> findOngoingByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE l.lineOfResearchId = :lineOfResearchId AND p.endDate IS NULL")
    Page<Project> findOngoingByLineOfResearchId(@Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.date = :year AND p.endDate IS NULL")
    Page<Project> findOngoingByDate(@Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE p.title LIKE %:title% AND l.lineOfResearchId = :lineOfResearchId AND p.endDate IS NULL")
    Page<Project> findOngoingByTitleContainingAndLineOfResearchId(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.title LIKE %:title% AND p.date = :year AND p.endDate IS NULL")
    Page<Project> findOngoingByTitleContainingAndDate(@Param("title") String title, @Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE l.lineOfResearchId = :lineOfResearchId AND p.date = :year AND p.endDate IS NULL")
    Page<Project> findOngoingByLineOfResearchIdAndDate(@Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.linesOfResearch l WHERE p.title LIKE %:title% AND l.lineOfResearchId = :lineOfResearchId AND p.date = :year AND p.endDate IS NULL")
    Page<Project> findOngoingByTitleContainingAndLineOfResearchIdAndDate(@Param("title") String title, @Param("lineOfResearchId") String lineOfResearchId, @Param("year") String year, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.endDate IS NULL")
    Page<Project> findOngoingAll(Pageable pageable);
}