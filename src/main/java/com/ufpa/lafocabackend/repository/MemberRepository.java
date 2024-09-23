package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findBySlug(String slug);

    boolean existsByEmail(String email);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto(m.memberId, m.displayName, m.slug, m.functionMember.name, m.memberPhoto.url) FROM Member m ORDER BY RAND()")
    Page<MemberSummaryDto> getMemberSummary(Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:yearClassId IS NULL OR m.yearClass = :yearClassId) " +
            "AND (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId)")
    Page<MemberResumed> findResumedMembersByFullNameAndYearClassIdAndFunctionId(@Param("fullName") String fullName, @Param("yearClassId") Long yearClassId, @Param("functionId") Long functionId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.fullName LIKE %:name% AND m.yearClass = :yearClassId")
    Page<MemberResumed> findResumedMembersByFullNameContainingAndYearClass(@Param("name") String name, @Param("yearClassId") Long yearClassId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId)")
    Page<MemberResumed> findResumedMembersByFullNameAndFunctionId(@Param("fullName") String fullName, @Param("functionId") Long functionId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.fullName LIKE %:name%")
    Page<MemberResumed> findResumedMembersByFullName(@Param("name") String name, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.yearClass = :yearClassId")
    Page<MemberResumed> findResumedMembersByYearClass(@Param("yearClassId") Long yearClassId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.functionMember.functionMemberId = :functionId")
    Page<MemberResumed> findResumedMembersByFunctionId(@Param("functionId") Long functionId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "WHERE m.yearClass = :yearClassId AND m.functionMember.functionMemberId = :functionId")
    Page<MemberResumed> findResumedMembersByYearClassAndFunctionId(@Param("yearClassId") Long yearClassId, @Param("functionId") Long functionId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m")
    Page<MemberResumed> listResumed(Pageable pageable);


    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:yearClassId IS NULL OR m.yearClass = :yearClassId) " +
            "AND (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId) " +
            "AND (:skillId IS NULL OR s.SkillId IN :skillIds)")
    Page<MemberResumed> findResumedMembersByFullNameAndYearClassIdAndFunctionIdAndSkillId(@Param("fullName") String fullName,
                                                                                          @Param("yearClassId") Long yearClassId,
                                                                                          @Param("functionId") Long functionId,
                                                                                          @Param("skillId") Long skillId,
                                                                                          Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:yearClassId IS NULL OR m.yearClass = :yearClassId) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByFullNameAndYearClassIdAndSkillId(@Param("fullName") String fullName,
                                                                             @Param("yearClassId") Long yearClassId,
                                                                             @Param("skillId") Long skillId,
                                                                             Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByFullNameAndFunctionIdAndSkillId(@Param("fullName") String fullName,
                                                                            @Param("functionId") Long functionId,
                                                                            @Param("skillId") Long skillId,
                                                                            Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:yearClassId IS NULL OR m.yearClass = :yearClassId) " +
            "AND (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByYearClassIdAndFunctionIdAndSkillId(@Param("yearClassId") Long yearClassId,
                                                                               @Param("functionId") Long functionId,
                                                                               @Param("skillId") Long skillId,
                                                                               Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersBySkillId(@Param("skillId") Long skillId, Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:fullName IS NULL OR m.fullName LIKE %:fullName%) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByFullNameAndSkillId(@Param("fullName") String fullName,
                                                               @Param("skillId") Long skillId,
                                                               Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:yearClassId IS NULL OR m.yearClass = :yearClassId) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByYearClassIdAndSkillId(@Param("yearClassId") Long yearClassId,
                                                                  @Param("skillId") Long skillId,
                                                                  Pageable pageable);

    @Query("SELECT new com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed(m.memberId, m.fullName, m.displayName, m.memberPhoto.url, m.functionMember.name, m.slug, m.email, m.yearClass, m.dateRegister) " +
            "FROM Member m " +
            "LEFT JOIN m.skills s " +
            "WHERE (:functionId IS NULL OR m.functionMember.functionMemberId = :functionId) " +
            "AND (:skillId IS NULL OR s.SkillId = :skillId)")
    Page<MemberResumed> findResumedMembersByFunctionIdAndSkillId(@Param("functionId") Long functionId,
                                                                 @Param("skillId") Long skillId,
                                                                 Pageable pageable);



}