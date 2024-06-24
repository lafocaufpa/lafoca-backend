package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LineOfResearchRepository extends JpaRepository<LineOfResearch, String> {
    @Query("SELECT l FROM LineOfResearch l WHERE l.name LIKE %:name%")
    Page<LineOfResearch> findByNameContaining(@Param("name") String name, Pageable pageable);
}
