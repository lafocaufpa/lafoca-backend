package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
