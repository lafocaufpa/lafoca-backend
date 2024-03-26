package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Lafoca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LafocaRepository extends JpaRepository<Lafoca, Long> {
}
