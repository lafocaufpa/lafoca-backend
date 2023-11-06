package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.Photo;
import com.ufpa.lafocabackend.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoStorageService photoStorageService;

    public PhotoService(PhotoRepository photoRepository, PhotoStorageService photoStorageService) {
        this.photoRepository = photoRepository;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public Photo save (Photo photo, InputStream inputStream ){

        PhotoStorageService.newPhoto newPhoto = PhotoStorageService.newPhoto.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .id(photo.getPhotoId())
                .inputStream(inputStream)
                .build();
        final Photo photoSaved = photoRepository.save(photo);
        photoRepository.flush();

        photoStorageService.armazenar(newPhoto);

        return photoSaved;
    }

    @Transactional
    public void delete (Long photoId){
        final Photo photo = getOrFail(photoId);
        this.photoRepository.deleteById(photo.getPhotoId());
    }

    private Photo getOrFail(Long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found: id " + photoId));
    }
}
