package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhotosRepository extends JpaRepository<UserPhoto, Long> {

}
