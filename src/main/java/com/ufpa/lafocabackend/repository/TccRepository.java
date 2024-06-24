package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Tcc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TccRepository extends JpaRepository<Tcc, Long> {

    @Query("SELECT t FROM Tcc t WHERE t.name LIKE %:name%")
    Page<Tcc> findByNameContaining(@Param("name") String name, Pageable pageable);
}
