package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.exception.EntityAlreadyRegisteredException;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.*;
import com.ufpa.lafocabackend.domain.model.dto.YearClassDTO;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.MemberPhotoRepository;
import com.ufpa.lafocabackend.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final FunctionMemberService functionMemberService;
    private final SkillService skillService;
    private final ModelMapper modelMapper;
    private final TccService tccService;
    private final ArticleService articleService;
    private final ProjectService projectService;
    private final MemberPhotoRepository memberPhotoRepository;
    private final PhotoStorageService photoStorageService;
    private final YearClassService yearClassService;

    public MemberService(MemberRepository memberRepository, FunctionMemberService functionMemberService, SkillService skillService, ModelMapper modelMapper, TccService tccService, ArticleService articleService, ProjectService projectService, MemberPhotoRepository memberPhotoRepository, PhotoStorageService photoStorageService, YearClassService yearClassService) {
        this.memberRepository = memberRepository;
        this.functionMemberService = functionMemberService;
        this.skillService = skillService;
        this.modelMapper = modelMapper;
        this.tccService = tccService;
        this.articleService = articleService;
        this.projectService = projectService;
        this.memberPhotoRepository = memberPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.yearClassService = yearClassService;
    }

    @Transactional
    public Member save(MemberInputDto memberInputDto) {

        if (memberRepository.existsByEmail(memberInputDto.getEmail())) {
            throw new EntityAlreadyRegisteredException(Member.class.getSimpleName(), memberInputDto.getEmail());
        }

        Member member = modelMapper.map(memberInputDto, Member.class);

        if (memberInputDto.getYearClassId() != null) {
            YearClassDTO yearClassDTO = yearClassService.read(memberInputDto.getYearClassId());
            YearClass yearClass = modelMapper.map(yearClassDTO, YearClass.class);
            member.setYearClass(yearClass);
        }

        if (memberInputDto.getFunctionMemberId() != null) {
            member.setFunctionMember(functionMemberService.read(memberInputDto.getFunctionMemberId()));
        }

        if (memberInputDto.getSkillsId() != null) {
            for (Long skillId : memberInputDto.getSkillsId()) {
                member.addSkill(skillService.getOrFail(skillId));
            }
        }

        if (memberInputDto.getProjectsId() != null) {
            for (String projectId : memberInputDto.getProjectsId()) {
                member.addProject(projectService.read(projectId));
            }
        }

        if (memberInputDto.getArticlesId() != null) {
            for (Long articleId : memberInputDto.getArticlesId()) {
                member.addArticles(articleService.read(articleId));
            }
        }

        if(memberInputDto.getTccId() != null){
            Long tccId = memberInputDto.getTccId();
            Tcc read = tccService.read(tccId);
            member.setTcc(read);
        }

        final Member memberSaved = memberRepository.save(member);
        return memberSaved;
    }

    @Transactional
    public Member update(String memberId, MemberInputDto memberInputDto) {
        Member member = read(memberId);
        modelMapper.map(memberInputDto, member);

        if (memberInputDto.getFunctionMemberId() != null) {
            member.setFunctionMember(functionMemberService.read(memberInputDto.getFunctionMemberId()));
        }

        if (memberInputDto.getSkillsId() != null) {
            Set<Skill> skills = new HashSet<>();
            for (Long skillId : memberInputDto.getSkillsId()) {
                skills.add(skillService.getOrFail(skillId));
            }
            member.setSkills(skills);
        }

        if (memberInputDto.getProjectsId() != null) {
            Set<Project> projects = new HashSet<>();
            for (String projectId : memberInputDto.getProjectsId()) {
                projects.add(projectService.read(projectId));
            }
            member.setProjects(projects);
        }

        if (memberInputDto.getArticlesId() != null) {
            Set<Article> articles = new HashSet<>();
            for (Long articleId : memberInputDto.getArticlesId()) {
                articles.add(articleService.read(articleId));
            }
            member.setArticles(articles);
        }

        if(memberInputDto.getTccId() != null){
            Long tccId = memberInputDto.getTccId();
            Tcc read = tccService.read(tccId);
            member.setTcc(read);
        } else {
            member.setTcc(null);
        }

        if(memberInputDto.getYearClassId() != null) {
            YearClass yearClass = yearClassService.getOrFail(memberInputDto.getYearClassId());
            member.setYearClass(yearClass);
        }

        return member;
    }

    @Deprecated
    public Page<Member> list(Pageable pageable) {

        return memberRepository.findAll(pageable);
    }

    public Page<MemberSummaryDto> listSummaryMember(Pageable pageable) {

        return memberRepository.getMemberSummary(pageable);
    }

    public Page<MemberResumed> listResumedMembers(Pageable pageable) {

        return memberRepository.listResumed(pageable);
    }

    public Page<MemberResumed> searchResumedMembersByFullNameYearClassIdFunctionIdAndSkillId(String fullName, Long yearClassId, Long functionId, Long skillId, Pageable pageable) {
        if (fullName != null && !fullName.isEmpty() && yearClassId != null && functionId != null && skillId != null) {
            return memberRepository.findResumedMembersByFullNameAndYearClassIdAndFunctionIdAndSkillId(fullName, yearClassId, functionId, skillId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && yearClassId != null && functionId != null) {
            return memberRepository.findResumedMembersByFullNameAndYearClassIdAndFunctionId(fullName, yearClassId, functionId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && yearClassId != null && skillId != null) {
            return memberRepository.findResumedMembersByFullNameAndYearClassIdAndSkillId(fullName, yearClassId, skillId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && functionId != null && skillId != null) {
            return memberRepository.findResumedMembersByFullNameAndFunctionIdAndSkillId(fullName, functionId, skillId, pageable);
        } else if (yearClassId != null && functionId != null && skillId != null) {
            return memberRepository.findResumedMembersByYearClassIdAndFunctionIdAndSkillId(yearClassId, functionId, skillId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && yearClassId != null) {
            return memberRepository.findResumedMembersByFullNameContainingAndYearClass(fullName, yearClassId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && functionId != null) {
            return memberRepository.findResumedMembersByFullNameAndFunctionId(fullName, functionId, pageable);
        } else if (fullName != null && !fullName.isEmpty() && skillId != null) {
            return memberRepository.findResumedMembersByFullNameAndSkillId(fullName, skillId, pageable);
        } else if (yearClassId != null && functionId != null) {
            return memberRepository.findResumedMembersByYearClassAndFunctionId(yearClassId, functionId, pageable);
        } else if (yearClassId != null && skillId != null) {
            return memberRepository.findResumedMembersByYearClassIdAndSkillId(yearClassId, skillId, pageable);
        } else if (functionId != null && skillId != null) {
            return memberRepository.findResumedMembersByFunctionIdAndSkillId(functionId, skillId, pageable);
        } else if (fullName != null && !fullName.isEmpty()) {
            return memberRepository.findResumedMembersByFullName(fullName, pageable);
        } else if (yearClassId != null) {
            return memberRepository.findResumedMembersByYearClass(yearClassId, pageable);
        } else if (functionId != null) {
            return memberRepository.findResumedMembersByFunctionId(functionId, pageable);
        } else if (skillId != null) {
            return memberRepository.findResumedMembersBySkillId(skillId, pageable);
        } else {
            return memberRepository.listResumed(pageable);
        }
    }

    public Member read(String memberId) {
        return getOrFail(memberId);
    }

    public Member readBySlug(String slug) {
        return memberRepository.findBySlug(slug).
                orElseThrow(() -> new EntityNotFoundException(Member.class.getSimpleName(), slug));
    }

    public void delete(String memberId) {

        read(memberId);

        String memberPhotoFileNameByPhotoId = memberPhotoRepository.findMemberPhotoFileNameByPhotoId(memberId);

        try {
            memberRepository.deleteById(memberId);

            var storagePhotoUtils = StoragePhotoUtils
                    .builder()
                    .fileName(memberPhotoFileNameByPhotoId)
                    .type(TypeEntityPhoto.Member).build();

            photoStorageService.deletar(storagePhotoUtils);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Member.class.getSimpleName(), memberId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Member.class.getSimpleName(), memberId);
        }

    }

    private Member getOrFail(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(Member.class.getSimpleName(), memberId));
    }

    @Transactional
    public void associateFunction(Long functionMemberId, String memberId) {

        final FunctionMember functionMember = functionMemberService.read(functionMemberId);
        final Member member = read(memberId);
        member.setFunctionMember(functionMember);
        memberRepository.save(member);
    }

    @Transactional
    public void associateSkill(String memberId, Long skillId) {
        final Member member = read(memberId);
        final Skill skill = skillService.getOrFail(skillId);
        member.addSkill(skill);
    }

    @Transactional
    public void disassociateSkill(String memberId, Long skillId) {
        final Member member = read(memberId);
        final Skill skill = skillService.getOrFail(skillId);
        member.removeSkill(skill);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }
}
