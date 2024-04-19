package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.NewsPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public NewsPhoto save(NewsPhoto photo, InputStream inputStream) throws IOException {

        final NewsPhoto photoSaved = newsPhotoRepository.save(photo);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .type(TypeEntityPhoto.News)
                .inputStream(inputStream)
                .build();

        final String url = photoStorageService.armazenar(newPhoto);

        photoSaved.setUrl(url);

        return photoSaved;
    }

    @Transactional
    public void delete(String newsId) {

        final String fileName = newsPhotoRepository.findFileName(newsId);

        newsPhotoRepository.removePhotoReference(newsId);
        newsPhotoRepository.deleteById(newsId);
        photoStorageService.deletar(StoragePhotoUtils.builder().type(TypeEntityPhoto.News).fileName(fileName).build());
    }

}
