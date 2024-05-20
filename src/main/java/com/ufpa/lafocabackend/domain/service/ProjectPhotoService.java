package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.FileWrapper;
import com.ufpa.lafocabackend.core.utils.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.ProjectPhoto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.ProjectPhotoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class ProjectPhotoService {

    private final PhotoStorageService photoStorageService;
    private final ProjectPhotoRepository projectPhotoRepository;
    private final ModelMapper modelMapper;


    public ProjectPhotoService(PhotoStorageService photoStorageService, ProjectPhotoRepository projectPhotoRepository, ModelMapper modelMapper) {
        this.projectPhotoRepository = projectPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void delete(Project project) {

        String projectId = project.getProjectId();

        String photoFilename = projectPhotoRepository.findProjectPhotoFileNameByPhotoId(projectId);
        projectPhotoRepository.removeProjectPhotoReference(projectId);
        projectPhotoRepository.deleteProjectPhotoByProjectId(projectId);
        var storagePhotoUtils = StoragePhotoUtils
                .builder()
                .fileName(photoFilename)
                .type(TypeEntityPhoto.Project).build();

        photoStorageService.deletar(storagePhotoUtils);

    }

    @Transactional
    public PhotoDto save(Project project, CustomMultipartFile photo) throws IOException {

        String originalPhotoFilename = createPhotoFilename(project.getSlug(), photo.getOriginalFilename());

        ProjectPhoto memberPhoto = new ProjectPhoto();
        memberPhoto.setPhotoId(project.getProjectId());
        memberPhoto.setFileName(originalPhotoFilename);
        memberPhoto.setSize(photo.getSize());
        memberPhoto.setContentType(photo.getContentType());

        final ProjectPhoto memberPhotoSaved = projectPhotoRepository.save(memberPhoto);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(memberPhoto.getFileName())
                .contentType(memberPhoto.getContentType())
                .contentLength(memberPhoto.getSize())
                .type(TypeEntityPhoto.Project)
                .inputStream(photo.getInputStream())
                .build();

        final String url = photoStorageService.armazenar(newPhoto);
        memberPhotoSaved.setUrl(url);

        project.setProjectPhoto(memberPhotoSaved);
        return modelMapper.map(memberPhotoSaved, PhotoDto.class);
    }


}
