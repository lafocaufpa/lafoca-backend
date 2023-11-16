package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.Photo;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;

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

    public RecoveredPhoto get(Long photId){

        final Photo photo = getOrFail(photId);

        final String fileName = photo.getFileName();

        final RecoveredPhoto recoveredPhoto = photoStorageService.recuperar(fileName);
        return recoveredPhoto;
    }

    @Transactional
    public void delete (Long photoId){
        final Photo photo = getOrFail(photoId);
        final String fileName = photo.getFileName();
        photoStorageService.deletar(fileName);
        this.photoRepository.deleteById(photo.getPhotoId());
    }

    private Photo getOrFail(Long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found: id " + photoId));
    }
}
