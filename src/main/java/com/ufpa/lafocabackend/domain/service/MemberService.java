package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.events.addedMemberEvent;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.*;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.TccDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.MemberRepository;
import com.ufpa.lafocabackend.repository.TccRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PhotoStorageService photoStorageService;
    private final FunctionMemberService functionMemberService;
    private final SkillService skillService;
    private final ModelMapper modelMapper;
    private final TccService tccService;
    private final ArticleService articleService;
    private final ProjectService projectService;
    private final TccRepository tccRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public MemberService(MemberRepository memberRepository, PhotoStorageService photoStorageService, FunctionMemberService functionMemberService, SkillService skillService, ModelMapper modelMapper, TccService tccService, ArticleService articleService, ProjectService projectService, TccRepository tccRepository) {
        this.memberRepository = memberRepository;
        this.photoStorageService = photoStorageService;
        this.functionMemberService = functionMemberService;
        this.skillService = skillService;
        this.modelMapper = modelMapper;
        this.tccService = tccService;
        this.articleService = articleService;
        this.projectService = projectService;
        this.tccRepository = tccRepository;
    }

    @Transactional
    public Member save(MemberInputDto memberInputDto) {
        Member member = modelMapper.map(memberInputDto, Member.class);

        if (memberInputDto.getFunctionMemberId() != null) {
            member.setFunctionMember(functionMemberService.read(memberInputDto.getFunctionMemberId()));
        }

        if (memberInputDto.getSkillsId() != null) {
            for (Long skillId : memberInputDto.getSkillsId()) {
                member.addSkill(skillService.getOrFail(skillId));
            }
        }

        if (memberInputDto.getProjects() != null) {
            for (String projectId : memberInputDto.getProjects()) {
                member.addProject(projectService.read(projectId));
            }
        }

        if (memberInputDto.getArticles() != null) {
            for (Long articleId : memberInputDto.getArticles()) {
                member.addArticles(articleService.read(articleId));
            }
        }

        if (memberInputDto.getTcc() != null) {
            final TccDto tccDto = memberInputDto.getTcc();
            final Tcc tcc = modelMapper.map(tccDto, Tcc.class);
            final Tcc tccSaved = tccService.save(tcc);
            member.setTcc(tccSaved);
        }

        final Member memberSaved = memberRepository.save(member);
        applicationEventPublisher.publishEvent(new addedMemberEvent(memberSaved));
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

        if (memberInputDto.getProjects() != null) {
            Set<Project> projects = new HashSet<>();
            for (String projectId : memberInputDto.getProjects()) {
                projects.add(projectService.read(projectId));
            }
            member.setProjects(projects);
        }

        if (memberInputDto.getArticles() != null) {
            Set<Article> articles = new HashSet<>();
            for (Long articleId : memberInputDto.getArticles()) {
                articles.add(articleService.read(articleId));
            }
            member.setArticles(articles);
        }

        if (memberInputDto.getTcc() != null) {

            final TccDto tccDto = memberInputDto.getTcc();
            final Tcc tcc = modelMapper.map(tccDto, Tcc.class);
            final Tcc tccSaved = tccService.save(tcc);
            member.setTcc(tccSaved);
        } else {
            if (member.getTcc() != null) {
                tccRepository.deleteById(member.getTcc().getTccId());
                member.setTcc(null);
            }
        }

        return memberRepository.save(member);
    }

    public Page<Member> list(Pageable pageable) {

        return memberRepository.findAll(pageable);
    }

    public Page<MemberSummaryDto> listSummaryMember(Pageable pageable) {

        return memberRepository.getMemberSummary(pageable);
    }

    public Member read(String memberId) {
        return getOrFail(memberId);
    }

    public Member readMemberBySlug(String slug) {
        return memberRepository.findMemberBySlug(slug).
                orElseThrow(() -> new EntityNotFoundException(Member.class.getSimpleName(), slug));
    }

    public void delete(String memberId) {

        try {
            memberRepository.deleteById(memberId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Member.class.getSimpleName(), memberId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Member.class.getSimpleName(), memberId);
        }

    }

    private Member getOrFail(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(getClass().getSimpleName(), memberId));
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
