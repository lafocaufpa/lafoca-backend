package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query("SELECT s FROM Skill s WHERE s.name LIKE %:name%")
    Page<Skill> findByNameContaining(@Param("name") String name, Pageable pageable);
}
