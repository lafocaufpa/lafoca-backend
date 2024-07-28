package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.Project;
import com.ufpa.lafocabackend.domain.model.ProjectPhoto;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
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

        String formatedOffsetDateTime = LafocaUtils.getFormatedOffsetDateTime();

        String idPhoto = formatedOffsetDateTime + project.getProjectId();

        String originalPhotoFilename = createPhotoFilename(idPhoto, photo.getOriginalFilename());

        String oldPhotoName = null;
        if(project.getProjectPhoto() != null){
            oldPhotoName = getFileNamePhoto(project);
        }
        ProjectPhoto projectPhoto = new ProjectPhoto();
        projectPhoto.setPhotoId(project.getProjectId());
        projectPhoto.setFileName(originalPhotoFilename);
        projectPhoto.setSize(photo.getSize());
        projectPhoto.setDataUpdate(formatedOffsetDateTime);
        projectPhoto.setContentType(photo.getContentType());

        final ProjectPhoto memberPhotoSaved = projectPhotoRepository.save(projectPhoto);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(projectPhoto.getFileName())
                .contentType(projectPhoto.getContentType())
                .contentLength(projectPhoto.getSize())
                .type(TypeEntityPhoto.Project)
                .inputStream(photo.getInputStream())
                .build();

        photoStorageService.deletar(StoragePhotoUtils.builder().fileName(oldPhotoName).type(TypeEntityPhoto.Member).build());
        final String url = photoStorageService.armazenar(newPhoto);
        memberPhotoSaved.setUrl(url);

        project.setProjectPhoto(memberPhotoSaved);
        return modelMapper.map(memberPhotoSaved, PhotoDto.class);
    }

    public String getFileNamePhoto(Project project) {
        return project.getProjectPhoto().getFileName();
    }

}
