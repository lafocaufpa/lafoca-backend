package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.name LIKE %:name%")
    Page<Group> findByNameContaining(@Param("name") String name, Pageable pageable);
}
