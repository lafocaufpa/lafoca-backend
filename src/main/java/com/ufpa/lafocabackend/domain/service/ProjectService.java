package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.MemberInfo;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.input.ProjectInputDto;
import com.ufpa.lafocabackend.repository.MemberRepository;
import com.ufpa.lafocabackend.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final LineOfResearchService lineOfResearchService;
    private final MemberRepository memberRepository;

    public ProjectService(ProjectRepository projectRepository, ModelMapper modelMapper, LineOfResearchService lineOfResearchService, MemberRepository memberRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.lineOfResearchService = lineOfResearchService;
        this.memberRepository = memberRepository;
    }

    public Project save (ProjectInputDto projectInputDto) {

        Project project = modelMapper.map(projectInputDto, Project.class);

        if(projectInputDto.getLineOfResearchIds() != null){
            for (String lineOfResearchId : projectInputDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                project.addLineOfResearch(lineOfResearch);
            }
        }

        Project savedProject = projectRepository.save(project);

        setMembersInProject(savedProject, projectInputDto.getMembers());

        return savedProject;
    }

    public Page<Project> list(String title, String lineOfResearchId, Integer year, Pageable pageable, boolean onGoing) {
        if (Boolean.TRUE.equals(onGoing)) {
            return filterOngoingProjects(title, lineOfResearchId, year, pageable);
        } else {
            return filterAllProjects(title, lineOfResearchId, year, pageable);
        }

    }

    public Page<Project> filterAllProjects(String title, String lineOfResearchId, Integer year, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return projectRepository.findByTitleContainingAndLineOfResearchIdAndDate(title, lineOfResearchId, String.valueOf(year), pageable);
        } else if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return projectRepository.findByTitleContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty() && year != null) {
            return projectRepository.findByTitleContainingAndDate(title, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return projectRepository.findByLineOfResearchIdAndDate(lineOfResearchId, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return projectRepository.findByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return projectRepository.findByTitleContaining(title, pageable);
        } else if (year != null) {
            return projectRepository.findByDate(String.valueOf(year), pageable);
        } else {
            return projectRepository.findAll(pageable);
        }
    }

    private Page<Project> filterOngoingProjects(String title, String lineOfResearchId, Integer year, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return projectRepository.findOngoingByTitleContainingAndLineOfResearchIdAndDate(title, lineOfResearchId, String.valueOf(year), pageable);
        } else if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return projectRepository.findOngoingByTitleContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty() && year != null) {
            return projectRepository.findOngoingByTitleContainingAndDate(title, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return projectRepository.findOngoingByLineOfResearchIdAndDate(lineOfResearchId, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return projectRepository.findOngoingByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return projectRepository.findOngoingByTitleContaining(title, pageable);
        } else if (year != null) {
            return projectRepository.findOngoingByDate(String.valueOf(year), pageable);
        } else {
            return projectRepository.findOngoingAll(pageable);
        }
    }

    public Project read (String projectId) {
        return getOrFail(projectId);
    }

    public Project update (String projectId, ProjectInputDto newProject) {

        // Obter o projeto atual
        final Project currentProject = read(projectId);
        Set<MemberInfo> currentMembers = currentProject.getMembers();  // Membros atuais

        // Mapear o newProject para o currentProject (exceto membros)
        modelMapper.map(newProject, currentProject);
        currentProject.setProjectId(projectId);  // Definir o ID do projeto para garantir

        // Atualizar linhas de pesquisa
        List<LineOfResearch> linesOfResearches = new ArrayList<>();
        if (newProject.getLineOfResearchIds() != null) {
            for (String lineOfResearchId : newProject.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                linesOfResearches.add(lineOfResearch);
            }
        }
        currentProject.setLinesOfResearch(linesOfResearches);

        // Gerenciar membros (adicionar/associar ou remover)
        Set<MemberInfo> newMembers = newProject.getMembers();

        // Remover membros que não estão mais associados
        Set<MemberInfo> membersToRemove = new HashSet<>(currentMembers);
        membersToRemove.removeAll(newMembers);  // Membros que devem ser removidos

        for (MemberInfo memberInfoToRemove : membersToRemove) {
            if (memberInfoToRemove.getSlug() != null) {
                Optional<Member> member = memberRepository.findBySlug(memberInfoToRemove.getSlug());
                if (member.isPresent()) {
                    Member foundMember = member.get();
                    foundMember.removeProject(currentProject);  // Remover a associação do membro ao projeto
                    memberRepository.save(foundMember);  // Persistir a alteração no banco
                }
            }
        }

        // Associar novos membros
        setMembersInProject(currentProject, newMembers);

        // Salvar o projeto atualizado
        return projectRepository.save(currentProject);
    }

    public void delete (String projectId) {

        try {
            projectRepository.deleteById(projectId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Project.class.getSimpleName(), projectId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Project.class.getSimpleName(), projectId);
        }
    }

    private Project getOrFail(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow( () -> new EntityNotFoundException(Project.class.getSimpleName(), projectId));
    }

    public Project readBySlug(String projectSlug) {
        return projectRepository.findBySlug(projectSlug).
                orElseThrow(() -> new EntityNotFoundException(Project.class.getSimpleName(), projectSlug));
    }

    @Transactional
    public void createSlugAll() {
        List<Project> all = projectRepository.findAll();
        for (Project project : all) {
            project.generateSlug();
            projectRepository.save(project);
        }
    }

    private void setMembersInProject(Project currentProject, Set<MemberInfo> members) {
        if (members != null && !members.isEmpty()) {
            currentProject.setMembers(members);
            for (MemberInfo memberInfo : members) {

                if (memberInfo.getSlug() != null) {
                    // Procurar o membro pelo slug no repositório
                    Optional<Member> member = memberRepository.findBySlug(memberInfo.getSlug());

                    if (member.isPresent()) {
                        Member foundMember = member.get();
                        foundMember.addProject(currentProject);
                        memberRepository.save(foundMember);
                    }
                }
            }
        }
    }

}
