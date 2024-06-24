package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.file.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.CacheUtil;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.dto.input.ProjectInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ProjectDto;
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
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public ResponseEntity<ProjectDto> add (@RequestBody @Valid ProjectInputDto projectInputDto) {

        final ProjectDto projectSaved = modelMapper.map(projectService.save(projectInputDto), ProjectDto.class);

        return ResponseEntity.ok(projectSaved);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> read (@PathVariable String projectId){

        final ProjectDto projectDto = modelMapper.map(projectService.read(projectId), ProjectDto.class);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(projectDto);
    }

    @GetMapping("/read/{projectSlug}")
    public ResponseEntity<ProjectDto> readBySlug(@PathVariable String projectSlug) {

        final Project project = projectService.readBySlug(projectSlug);
        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(projectDto);
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDto>> list(
            @RequestParam(value = "title", required = false) String title,
            Pageable pageable) {

        Page<Project> projectPage;

        if (title != null && !title.isEmpty()) {
            projectPage = projectService.searchByTitle(title, pageable);
        } else {
            projectPage = projectService.list(pageable);
        }

        Type listType = new TypeToken<List<ProjectDto>>() {}.getType();
        List<ProjectDto> map = modelMapper.map(projectPage.getContent(), listType);
        Page<ProjectDto> projects = new PageImpl<>(map, pageable, projectPage.getTotalElements());

        return CacheUtil.createCachedResponseProject(projects);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> update (@PathVariable String projectId, @RequestBody @Valid ProjectInputDto newProject){

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
    public ResponseEntity<PhotoDto> addPhoto(Part file, @PathVariable String projectId) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);
        Project member = projectService.read(projectId);
        return ResponseEntity.ok(projectPhotoService.save(member, customFile));
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping(value = "/read/{memberSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @DeleteMapping(value = "/read/{projectSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String projectSlug) {

        Project project = projectService.readBySlug(projectSlug);
        projectPhotoService.delete(project);

        return ResponseEntity.noContent().build();
    }

}
