package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.ProjectDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ProjectSummaryDto;
import com.ufpa.lafocabackend.domain.service.ProjectService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;

    public ProjectController(ProjectService projectService, ModelMapper modelMapper) {
        this.projectService = projectService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<ProjectDto> add (@RequestBody ProjectDto projectDto) {

        final ProjectDto projectSaved = modelMapper.map(projectService.save(projectDto), ProjectDto.class);

        return ResponseEntity.ok(projectSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> read (@PathVariable Long projectId){

        final ProjectDto projectDto = modelMapper.map(projectService.read(projectId), ProjectDto.class);
        return ResponseEntity.ok(projectDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<ProjectDto>> list (){

        final List<Project> list = projectService.list();

        Type listType = new TypeToken<List<ProjectDto>>() {

        }.getType();

        final List<ProjectDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/summarized")
    public ResponseEntity<Page<ProjectSummaryDto>> listProjectsSummarized (@PageableDefault(size = 7) Pageable pageable) {

        final Page<ProjectSummaryDto> projectsSummarizedDtos = projectService.listSummaryProjects(pageable);
        Page<ProjectSummaryDto> projectsSummarizedPage = new PageImpl<>(projectsSummarizedDtos.getContent(), pageable, projectsSummarizedDtos.getTotalElements());
        return ResponseEntity.ok(projectsSummarizedPage);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update (@PathVariable Long projectId, @RequestBody ProjectDto newProject){

        final ProjectDto projectUpdated = modelMapper.map(projectService.update(projectId, newProject), ProjectDto.class);
        return ResponseEntity.ok(projectUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete (@PathVariable Long projectId){

        projectService.delete(projectId);

        return ResponseEntity.noContent().build();
    }
}
