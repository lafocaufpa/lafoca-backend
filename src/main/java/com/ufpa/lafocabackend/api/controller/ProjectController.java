package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.ProjectDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ProjectSummaryDto;
import com.ufpa.lafocabackend.domain.service.ProjectPhotoService;
import com.ufpa.lafocabackend.domain.service.ProjectService;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;
    private final ProjectPhotoService projectPhotoService;

    public ProjectController(ProjectService projectService, ModelMapper modelMapper, ProjectPhotoService projectPhotoService) {
        this.projectService = projectService;
        this.modelMapper = modelMapper;
        this.projectPhotoService = projectPhotoService;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<ProjectDto> add (@RequestBody @Valid ProjectDto projectDto) {

        final ProjectDto projectSaved = modelMapper.map(projectService.save(projectDto), ProjectDto.class);

        return ResponseEntity.ok(projectSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> read (@PathVariable String projectId){

        final ProjectDto projectDto = modelMapper.map(projectService.read(projectId), ProjectDto.class);
        return ResponseEntity.ok(projectDto);
    }

    @GetMapping("/search/{projectSlug}")
    public ResponseEntity<ProjectDto> readBySlug(@PathVariable String projectSlug) {

        final Project project = projectService.readBySlug(projectSlug);
        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        return ResponseEntity.ok(projectDto);
    }

    @CheckSecurityPermissionMethods.Level1
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

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update (@PathVariable String projectId, @RequestBody ProjectDto newProject){

        final ProjectDto projectUpdated = modelMapper.map(projectService.update(projectId, newProject), ProjectDto.class);
        return ResponseEntity.ok(projectUpdated);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete (@PathVariable String projectId){

        projectService.delete(projectId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping(value = "/{projectId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(Part part, @PathVariable String projectId) throws IOException {

        var file = new StandardCustomMultipartFile(part);
        Project member = projectService.read(projectId);
        return ResponseEntity.ok(projectPhotoService.save(member, file));
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping(value = "/search/{memberSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhotoBySlug(Part file, @PathVariable String memberSlug) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);
        Project member = projectService.readBySlug(memberSlug);
        return ResponseEntity.ok(projectPhotoService.save(member, customFile));
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping(value = "/{projectId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String projectId) {

        Project project = projectService.read(projectId);
        projectPhotoService.delete(project);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping(value = "/search/{projectSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String projectSlug) {

        Project project = projectService.readBySlug(projectSlug);
        projectPhotoService.delete(project);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/generate-slug")
    public String generateSlug() {

        projectService.createSlugAll();
        return "<h1>Deu certo!</h1>";
    }
}
