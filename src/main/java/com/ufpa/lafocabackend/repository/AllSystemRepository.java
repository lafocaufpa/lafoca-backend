package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.AllSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllSystemRepository extends JpaRepository<AllSystem, Long> {
}
