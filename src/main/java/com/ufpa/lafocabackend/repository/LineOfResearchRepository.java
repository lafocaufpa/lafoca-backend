package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineOfResearchRepository extends JpaRepository<LineOfResearch, String> {

}
