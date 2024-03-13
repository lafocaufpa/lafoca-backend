package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Tcc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TccRepository extends JpaRepository<Tcc, Long> {
}
