package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
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

    public Page<Project> list (Pageable pageable){

        return projectRepository.findAll(pageable);
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
