package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s.photo.userPhotoId FROM Student s WHERE s.studentId = :studentId")
    String getUserPhotoIdByStudentId(String studentId);

    @Query("SELECT up.fileName FROM UserPhoto up WHERE up.userPhotoId = :userPhotoId")
    String findFileNameByUserPhotoId(String userPhotoId);
}
