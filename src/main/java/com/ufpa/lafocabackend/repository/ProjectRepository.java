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


}
