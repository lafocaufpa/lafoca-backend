package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPhoto up WHERE up.userPhotoId = :photoId")
    void deletePhotoByUserId(String photoId);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.userPhoto = null WHERE u.userId = :userId")
    void removePhotoReference(@Param("userId") String userId);

    @Query("SELECT u.userPhoto.userPhotoId FROM User u WHERE u.userId = :userId")
    String getUserPhotoIdByUserId(String userId);

    @Query("SELECT up.fileName FROM UserPhoto up WHERE up.userPhotoId = :userPhotoId")
    String findFileNameByUserPhotoId(String userPhotoId);
}
