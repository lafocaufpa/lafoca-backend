package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionStudentRepository extends JpaRepository<FunctionStudent, Long> {
}
