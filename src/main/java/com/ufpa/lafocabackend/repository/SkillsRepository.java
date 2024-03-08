package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillsRepository extends JpaRepository<Skills, Long> {
}
