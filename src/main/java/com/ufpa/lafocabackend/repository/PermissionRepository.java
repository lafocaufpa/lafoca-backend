package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long > {

    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:name% ")
    Page<Permission> findByNameContaining(@Param("name") String name, Pageable pageable);

}
