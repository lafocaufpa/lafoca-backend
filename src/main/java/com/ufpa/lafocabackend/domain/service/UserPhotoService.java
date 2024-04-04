package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.UserPhoto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.infrastructure.service.StorageUtils;
import com.ufpa.lafocabackend.repository.UserPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
public class UserPhotoService {


    private final PhotoStorageService photoStorageService;
    private final UserPhotoRepository userPhotoRepository;

    public UserPhotoService(PhotoStorageService photoStorageService, UserPhotoRepository userPhotoRepository) {
        this.userPhotoRepository = userPhotoRepository;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public UserPhoto save(UserPhoto photo, InputStream inputStream) {

        final UserPhoto photoSaved = userPhotoRepository.save(photo);

        StorageUtils newPhoto = StorageUtils.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .type(StorageUtils.FileType.TypeUser)
                .inputStream(inputStream)
                .build();

        final String url = photoStorageService.armazenar(newPhoto);

        photoSaved.setUrl(url);

        return photoSaved;
    }

    public RecoveredPhoto get(String fileName) {

        return photoStorageService.recuperar(fileName);
    }

    @Transactional
    public void delete(String userId) {

        final String photoId = userPhotoRepository.getUserPhotoIdByUserId(userId);
        final String fileName = userPhotoRepository.findFileNameByUserPhotoId(photoId);
        userPhotoRepository.removePhotoReference(userId);
        userPhotoRepository.deletePhotoByUserId(photoId);

        final StorageUtils storageUtils = StorageUtils.builder().fileName(fileName).type(StorageUtils.FileType.TypeUser).build();

        photoStorageService.deletar(storageUtils);
    }

}
