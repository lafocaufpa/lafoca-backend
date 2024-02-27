package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPhoto up WHERE up.userPhotoId = :photoId")
    void deletePhotoByUserId(String photoId);

    @Transactional
    @Modifying
    @Query("UPDATE Student s SET s.photo = null WHERE s.studentId = :studentId")
    void removePhotoReference(@Param("studentId") Long studentId);

    @Query("SELECT s.photo.userPhotoId FROM Student s WHERE s.studentId = :studentId")
    String getUserPhotoIdByStudentId(Long studentId);

    @Query("SELECT up.fileName FROM UserPhoto up WHERE up.userPhotoId = :userPhotoId")
    String findFileNameByUserPhotoId(String userPhotoId);
}
