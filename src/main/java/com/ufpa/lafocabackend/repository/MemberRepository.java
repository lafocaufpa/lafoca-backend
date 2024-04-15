package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPhoto up WHERE up.userPhotoId = :photoId")
    void deletePhotoByUserId(String photoId);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.photo = null WHERE m.memberId = :memberId")
    void removePhotoReference(@Param("memberId") String memberId);

    @Query("SELECT m.photo.userPhotoId FROM Member m WHERE m.memberId = :memberId")
    String getUserPhotoIdByMemberId(String memberId);

    @Query("SELECT up.fileName FROM UserPhoto up WHERE up.userPhotoId = :userPhotoId")
    String findFileNameByUserPhotoId(String userPhotoId);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto(m.memberId, m.name, m.slug, m.functionMember.name, m.photo.url) FROM Member m ORDER BY RAND()")
    Page<MemberSummaryDto> getMemberSummary(Pageable pageable);

    Optional<Member> findMemberBySlug(String slug);

}
