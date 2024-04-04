package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findGroupByName(String name);
}
