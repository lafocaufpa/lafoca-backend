package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.exception.EntityAlreadyRegisteredException;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.*;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.ArticleRepository;
import com.ufpa.lafocabackend.repository.MemberPhotoRepository;
import com.ufpa.lafocabackend.repository.MemberRepository;
import com.ufpa.lafocabackend.repository.ProjectRepository;
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
    private final ArticleRepository articleRepository;
    private final ProjectRepository projectRepository;

    public MemberService(MemberRepository memberRepository, FunctionMemberService functionMemberService, SkillService skillService, ModelMapper modelMapper, TccService tccService, ArticleService articleService, ProjectService projectService, MemberPhotoRepository memberPhotoRepository, PhotoStorageService photoStorageService, ArticleRepository articleRepository, ProjectRepository projectRepository) {
        this.memberRepository = memberRepository;
        this.functionMemberService = functionMemberService;
        this.skillService = skillService;
        this.modelMapper = modelMapper;
        this.tccService = tccService;
        this.articleService = articleService;
        this.projectService = projectService;
        this.memberPhotoRepository = memberPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.articleRepository = articleRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Member save(MemberInputDto memberInputDto) {

        if (memberRepository.existsByEmail(memberInputDto.getEmail())) {
            throw new EntityAlreadyRegisteredException(Member.class.getSimpleName(), memberInputDto.getEmail());
        }

        Member member = modelMapper.map(memberInputDto, Member.class);

        if (memberInputDto.getYearClassId() != null) {
            member.setYearClass(memberInputDto.getYearClassId());
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
            memberRepository.save(member);
            for (String projectId : memberInputDto.getProjectsId()) {
                Project project = projectService.read(projectId);
                member.addProject(project);
                project.addMember(member);
            }
        }

        if (memberInputDto.getArticlesId() != null) {
            memberRepository.save(member);
            for (Long articleId : memberInputDto.getArticlesId()) {
                Article article = articleService.read(articleId);
                member.addArticles(article);
                article.addMember(member);
            }
        }

        if(memberInputDto.getTccId() != null){
            Long tccId = memberInputDto.getTccId();
            Tcc read = tccService.read(tccId);
            read.setNameMember(member.getFullName());
            read.setSlugMember(member.getSlug());
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
            Set<Project> currentProjects = member.getProjects(); // Projetos associados atualmente no banco de dados

            // Verifica se há mudanças no número de projetos
            if (currentProjects.size() != memberInputDto.getProjectsId().size()) {
                Set<Project> newProjects = new HashSet<>();
                for (String projectId : memberInputDto.getProjectsId()) {
                    Project project = projectService.read(projectId);
                    newProjects.add(project);

                    // Obter o conjunto de MemberInfo do projeto
                    Set<MemberInfo> memberInfos = project.getMembers();

                    // Verificar se o membro já está no projeto, baseado no slug
                    MemberInfo existingMemberInfo = null;
                    for (MemberInfo memberInfo : memberInfos) {
                        if (memberInfo.getSlug() != null && memberInfo.getSlug().equals(member.getSlug())) {
                            existingMemberInfo = memberInfo;
                            break;
                        }
                    }

                    // Se o membro ainda não estiver associado ao projeto, adicionar um novo MemberInfo
                    if (existingMemberInfo == null) {
                        MemberInfo newMemberInfo = new MemberInfo();
                        newMemberInfo.setName(member.getFullName());

                        // Se o membro for interno (tem slug)
                        if (member.getSlug() != null) {
                            newMemberInfo.setSlug(member.getSlug());
                        }

                        // Adicionar o novo MemberInfo ao projeto
                        memberInfos.add(newMemberInfo);
                        projectRepository.save(project);  // Persistir as alterações no projeto
                    }
                }

                Set<Project> oldProjects = member.getProjects();

                // Cria uma lista para armazenar os projetos que estão apenas em oldProjects
                Set<Project> projectsNotInNewList = new HashSet<>(oldProjects);

                // Retira todos os projetos presentes em newProjects da lista de oldProjects
                projectsNotInNewList.removeAll(newProjects);

                for (Project removedProject : projectsNotInNewList) {
                    // Obter o conjunto de MemberInfo do projeto
                    Set<MemberInfo> memberInfos = removedProject.getMembers();

                    // Encontra o MemberInfo que corresponde ao membro que está sendo removido
                    MemberInfo memberInfoToRemove = null;
                    for (MemberInfo memberInfo : memberInfos) {
                        if (memberInfo.getSlug() != null && memberInfo.getSlug().equals(member.getSlug())) {
                            memberInfoToRemove = memberInfo;
                            break;
                        }
                    }

                    // Se foi encontrado o MemberInfo correspondente, remover da lista
                    if (memberInfoToRemove != null) {
                        memberInfos.remove(memberInfoToRemove);
                    }

                    projectRepository.save(removedProject);  // Persistir as alterações no projeto removido
                }

                // Atualizar os projetos do membro
                member.setProjects(newProjects);
            }
        }

        if (memberInputDto.getArticlesId() != null) {
            Set<Article> currentArticles = member.getArticles(); // Artigos associados atualmente no banco de dados

            // Verifica se há mudança no número de artigos
            if (currentArticles.size() != memberInputDto.getArticlesId().size()) {
                Set<Article> newArticles = new HashSet<>();
                for (Long articleId : memberInputDto.getArticlesId()) {

                    Article article = articleService.read(articleId);
                    newArticles.add(article);

                    // Obter o conjunto de MemberInfo do artigo
                    Set<MemberInfo> memberInfos = article.getMembers();

                    // Verificar se o membro já está no article, baseado no slug
                    MemberInfo existingMemberInfo = null;
                    for (MemberInfo memberInfo : memberInfos) {
                        if (memberInfo.getSlug() != null && memberInfo.getSlug().equals(member.getSlug())) {
                            existingMemberInfo = memberInfo;
                            break;
                        }
                    }

                    // Se o membro ainda não estiver associado ao artigo, adicionar um novo MemberInfo
                    if (existingMemberInfo == null) {
                        MemberInfo newMemberInfo = new MemberInfo();
                        newMemberInfo.setName(member.getFullName());

                        // Se o membro for interno (tem slug)
                        if (member.getSlug() != null) {
                            newMemberInfo.setSlug(member.getSlug());
                        }

                        // Adicionar o novo MemberInfo ao artigo
                        memberInfos.add(newMemberInfo);
                        articleRepository.save(article);  // Persistir as alterações no artigo
                    }
                }

                Set<Article> oldArticles = member.getArticles();

                // Cria uma lista para armazenar os artigos que estão apenas em oldArticles
                Set<Article> articlesNotInNewList = new HashSet<>(oldArticles);

                // Retira todos os artigos presentes em newArticles da lista de oldArticles
                articlesNotInNewList.removeAll(newArticles);

                for (Article removedArticle : articlesNotInNewList) {
                    // Obtem o conjunto de MemberInfo do artigo
                    Set<MemberInfo> memberInfos = removedArticle.getMembers();

                    // Encontra o MemberInfo que corresponde ao membro que está sendo removido
                    MemberInfo memberInfoToRemove = null;
                    for (MemberInfo memberInfo : memberInfos) {
                        if (memberInfo.getSlug() != null && memberInfo.getSlug().equals(member.getSlug())) {
                            memberInfoToRemove = memberInfo;
                            break;
                        }
                    }

                    // Se foi encontrado o MemberInfo correspondente, remover da lista
                    if (memberInfoToRemove != null) {
                        memberInfos.remove(memberInfoToRemove);
                    }

                    articleRepository.save(removedArticle);
                }

                // Atualizar os artigos do membro
                member.setArticles(newArticles);
            }
        }



        if(memberInputDto.getTccId() != null){
            Tcc read = tccService.read(memberInputDto.getTccId());
            read.setSlugMember(member.getSlug());
            read.setNameMember(member.getFullName());
            member.setTcc(read);
        } else {
            Tcc tcc = member.getTcc();
            if(tcc != null){
                member.getTcc().setNameMember(null);
                member.getTcc().setSlugMember(null);
                member.setTcc(null);
            }
        }

        if(memberInputDto.getYearClassId() != null) {
            member.setYearClass(member.getYearClass());
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
