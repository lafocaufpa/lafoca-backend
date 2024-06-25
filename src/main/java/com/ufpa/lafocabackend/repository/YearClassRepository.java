package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.YearClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YearClassRepository extends JpaRepository<YearClass, Long> {
}