package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface NewsPhotoRepository extends JpaRepository<NewsPhoto, Long> {


    @Transactional
    @Modifying
    @Query("UPDATE News n SET n.newsPhoto = null WHERE n.newsId = :newsId")
    void removePhotoReference(@Param("newsId") Long newsId);

    @Query("SELECT np.fileName FROM NewsPhoto np WHERE np.newsPhotoId = :newsPhotoId")
    String findFileName(Long newsPhotoId);

}
