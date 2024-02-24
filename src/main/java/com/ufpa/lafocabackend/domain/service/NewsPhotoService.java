package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.infrastructure.service.StorageUtils;
import com.ufpa.lafocabackend.repository.NewsPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Service
public class NewsPhotoService {


    private final PhotoStorageService photoStorageService;
    private final NewsPhotoRepository newsPhotoRepository;

    public NewsPhotoService(PhotoStorageService photoStorageService, NewsPhotoRepository newsPhotoRepository) {
        this.newsPhotoRepository = newsPhotoRepository;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public NewsPhoto save(NewsPhoto photo, InputStream inputStream) {

        final NewsPhoto photoSaved = newsPhotoRepository.save(photo);

        StorageUtils newPhoto = StorageUtils.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .type(StorageUtils.FileType.TypeNews)
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
    public void delete(Long newsId) {

        final String fileName = newsPhotoRepository.findFileName(newsId);

        newsPhotoRepository.removePhotoReference(newsId);
        newsPhotoRepository.deleteById(newsId);
        photoStorageService.deletar(StorageUtils.builder().type(StorageUtils.FileType.TypeNews).fileName(fileName).build());
    }

}
