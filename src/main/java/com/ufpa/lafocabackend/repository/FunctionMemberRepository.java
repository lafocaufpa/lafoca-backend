package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.FunctionMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionMemberRepository extends JpaRepository<FunctionMember, Long> {
    @Query("SELECT fm FROM FunctionMember fm WHERE fm.name LIKE %:name%")
    Page<FunctionMember> findByNameContaining(@Param("name") String name, Pageable pageable);
}
