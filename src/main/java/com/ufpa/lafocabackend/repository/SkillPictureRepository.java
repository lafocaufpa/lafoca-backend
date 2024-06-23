package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.MemberPhoto;
import com.ufpa.lafocabackend.domain.model.SkillPicture;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillPictureRepository extends JpaRepository<SkillPicture, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM SkillPicture sp WHERE sp.skillPictureId = :skillPictureId")
    void deleteSkillPictureBySkillId(Long skillPictureId);


    @Transactional
    @Modifying
    @Query("UPDATE Skill s SET s.skillPicture = null WHERE s.SkillId = :SkillId")
    void removeSkillPictureReference(Long SkillId);
    

    @Transactional
    @Query("SELECT sp.fileName FROM SkillPicture sp WHERE sp.skillPictureId = :skillPictureId")
    String findSkillPictureFileNameBySkillPictureId(Long skillPictureId);
}
