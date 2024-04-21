package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.ProjectDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ProjectSummaryDto;
import com.ufpa.lafocabackend.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public ProjectService(ProjectRepository projectRepository, ModelMapper modelMapper) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    public Project save (ProjectDto projectDto) {

        return projectRepository.save(modelMapper.map(projectDto, Project.class));
    }

    public List<Project> list (){

        return projectRepository.findAll();
    }

    public Project read (String projectId) {
        return getOrFail(projectId);
    }

    public Project update (String projectId, ProjectDto newProject) {

        final Project currentProject = read(projectId);

        modelMapper.map(newProject, currentProject);
        currentProject.setProjectId(projectId);

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

    public Page<ProjectSummaryDto> listSummaryProjects(Pageable pageable) {

        return projectRepository.getProjectSummary(pageable);
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
