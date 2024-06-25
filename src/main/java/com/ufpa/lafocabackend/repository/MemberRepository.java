package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.YearClass;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
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
    @Query("DELETE FROM MemberPhoto up WHERE up.photoId = :photoId")
    void deletePhotoByUserId(String photoId);

    @Transactional
    @Modifying
    @Query("UPDATE Member m SET m.memberPhoto = null WHERE m.memberId = :memberId")
    void removePhotoReference(@Param("memberId") String memberId);

    @Query("SELECT m.memberPhoto.photoId FROM Member m WHERE m.memberId = :memberId")
    String getUserPhotoIdByMemberId(String memberId);

    @Query("SELECT up.fileName FROM MemberPhoto up WHERE up.photoId = :userPhotoId")
    String findFileNameByUserPhotoId(String userPhotoId);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto(m.memberId, m.displayName, m.slug, m.functionMember.name, m.memberPhoto.url) FROM Member m ORDER BY RAND()")
    Page<MemberSummaryDto> getMemberSummary(Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.functionMember.name, m.slug, m.email, m.yearClass.year, m.dateRegister) FROM Member m")
    Page<MemberResumed> listMembers(Pageable pageable);

    boolean existsByEmail (String email);

    Optional<Member> findBySlug(String slug);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.functionMember.name, m.slug, m.email, m.yearClass.year, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.fullName LIKE %:fullName%")
    Page<MemberResumed> findResumedMembersByFullNameContaining(@Param("fullName") String fullName, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.functionMember.name, m.slug, m.email, m.yearClass.year, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.yearClass = :yearClass")
    Page<MemberResumed> findResumedMembersByYearClass(@Param("yearClass") YearClass yearClass, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.functionMember.name, m.slug, m.email, m.yearClass.year, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.fullName LIKE %:name% AND m.yearClass = :yearClass")
    Page<MemberResumed> findResumedMembersByFullNameContainingAndYearClass(@Param("name") String name, @Param("yearClass") YearClass yearClass, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.functionMember.name, m.slug, m.email, m.yearClass.year, m.dateRegister) " +
            "FROM Member m " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:yearClassId IS NULL OR m.yearClass.yearClassId = :yearClassId)")
    Page<MemberResumed> findResumedMembersByFullNameAndYearClassId(@Param("fullName") String fullName, @Param("yearClassId") Long yearClassId, Pageable pageable);


}
