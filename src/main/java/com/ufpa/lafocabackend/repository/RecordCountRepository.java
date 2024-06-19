package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.RecordCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordCountRepository extends JpaRepository<RecordCount, Long> {
}