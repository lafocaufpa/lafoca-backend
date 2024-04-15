package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.FunctionMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionMemberRepository extends JpaRepository<FunctionMember, Long> {
}
