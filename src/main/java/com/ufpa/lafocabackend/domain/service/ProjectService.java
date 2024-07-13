package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.MemberInfo;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.input.ProjectInputDto;
import com.ufpa.lafocabackend.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final LineOfResearchService lineOfResearchService;

    public ProjectService(ProjectRepository projectRepository, ModelMapper modelMapper, LineOfResearchService lineOfResearchService) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.lineOfResearchService = lineOfResearchService;
    }

    public Project save (ProjectInputDto projectInputDto) {

        Project project = modelMapper.map(projectInputDto, Project.class);

        if(projectInputDto.getLineOfResearchIds() != null){
            for (String lineOfResearchId : projectInputDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                project.addLineOfResearch(lineOfResearch);
            }
        }

        return projectRepository.save(project);
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

        final Project currentProject = read(projectId);

        modelMapper.map(newProject, currentProject);
        currentProject.setProjectId(projectId);

        List<LineOfResearch> linesOfResearch = new ArrayList<>();

        for(String id: newProject.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(id);
            linesOfResearch.add(lineOfResearch);
        }

        Set<MemberInfo> members = newProject.getMembers();
        currentProject.setMembers(members);

        currentProject.setLinesOfResearch(linesOfResearch);

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
}
