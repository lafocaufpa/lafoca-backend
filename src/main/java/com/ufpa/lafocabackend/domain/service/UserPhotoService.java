package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.Photo;
import com.ufpa.lafocabackend.domain.model.UserPhoto;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.repository.PhotoRepository;
import com.ufpa.lafocabackend.repository.UserPhotosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
public class UserPhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoStorageService photoStorageService;
    private final UserPhotosRepository userPhotosRepository;

    public UserPhotoService(PhotoRepository photoRepository, PhotoStorageService photoStorageService, UserPhotosRepository userPhotosRepository) {
        this.photoRepository = photoRepository;
        this.photoStorageService = photoStorageService;
        this.userPhotosRepository = userPhotosRepository;
    }

    @Transactional
    public Photo save(Photo photo, InputStream inputStream) {

        final Photo photoSaved = photoRepository.save(photo);
        photoRepository.flush();

        PhotoStorageService.newPhoto newPhoto = PhotoStorageService.newPhoto.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .id(photo.getPhotoId())
                .inputStream(inputStream)
                .build();

        final String url = photoStorageService.armazenar(newPhoto);

        photoSaved.setUrl(url);

        return photoSaved;
    }

    @Transactional
    public UserPhoto save(UserPhoto photo, InputStream inputStream) {

//        final Photo photoSaved = photoRepository.save(photo);
//        photoRepository.flush();

        final UserPhoto photoSaved = userPhotosRepository.save(photo);

        PhotoStorageService.newPhoto newPhoto = PhotoStorageService.newPhoto.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .newsOrUser(photoSaved)
                .inputStream(inputStream)
                .build();

        final String url = photoStorageService.armazenar(newPhoto);

        photoSaved.setUrl(url);

        return photoSaved;
    }

    public RecoveredPhoto get(String fileName) {

        final RecoveredPhoto recoveredPhoto = photoStorageService.recuperar(fileName);
        return recoveredPhoto;
    }

    @Transactional
    public void delete(String photoId) {
        final Photo photo = getOrFail(photoId);
        final String fileName = photo.getFileName();
        photoStorageService.deletar(fileName);
        this.photoRepository.deleteById(photo.getPhotoId());
    }

    private Photo getOrFail(String photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found: id " + photoId));
    }

    public Photo getByUserId (String userId){
        return null;
    }
}
