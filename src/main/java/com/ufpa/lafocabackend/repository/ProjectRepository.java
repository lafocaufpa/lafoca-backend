package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.output.ProjectSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.ProjectSummaryDto(p.projectId, p.type, p.title, p.description, p.completed, p.year, p.projectPhoto.url) FROM Project p")
    Page<ProjectSummaryDto> getProjectSummary(Pageable pageable);

    Optional<Project> findBySlug(String slug);
}
