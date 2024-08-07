package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.NewsPhotoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class NewsPhotoService {


    private final PhotoStorageService photoStorageService;
    private final NewsPhotoRepository newsPhotoRepository;
    private final ModelMapper modelMapper;
    public NewsPhotoService(PhotoStorageService photoStorageService, NewsPhotoRepository newsPhotoRepository, ModelMapper modelMapper ) {
        this.newsPhotoRepository = newsPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PhotoDto save(News news, CustomMultipartFile photo) throws IOException {

        String formatedOffsetDateTime = LafocaUtils.getFormatedOffsetDateTime();

        String idPhoto = formatedOffsetDateTime + news.getNewsId();

        String originalPhotoFilename = createPhotoFilename(idPhoto, photo.getOriginalFilename());

        String oldPhotoName = null;
        if(news.getNewsPhoto() != null){
            oldPhotoName = getFileNamePhoto(news);
        }
        NewsPhoto newsPhoto = new NewsPhoto();
        newsPhoto.setPhotoId(news.getNewsId());
        newsPhoto.setFileName(originalPhotoFilename);
        newsPhoto.setSize(photo.getSize());
        newsPhoto.setDataUpdate(formatedOffsetDateTime);
        newsPhoto.setContentType(photo.getContentType());

        NewsPhoto newsPhotoSaved = newsPhotoRepository.save(newsPhoto);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(newsPhoto.getFileName())
                .contentType(newsPhoto.getContentType())
                .contentLength(newsPhoto.getSize())
                .type(TypeEntityPhoto.News)
                .inputStream(photo.getInputStream())
                .build();

        photoStorageService.deletar(StoragePhotoUtils.builder().fileName(oldPhotoName).type(TypeEntityPhoto.News).build());
        final String url = photoStorageService.armazenar(newPhoto);
        newsPhotoSaved.setUrl(url);

        news.setNewsPhoto(newsPhotoSaved);
        return modelMapper.map(newsPhotoSaved, PhotoDto.class);
    }

    @Transactional
    public void delete(News news) {

        String newsId = news.getNewsId();

        String photoFilename = newsPhotoRepository.findNewsPhotoFileNameByPhotoId(newsId);
        newsPhotoRepository.removeNewsPhotoReference(newsId);
        newsPhotoRepository.deleteNewsPhotoByNewsId(newsId);

        var storagePhotoUtils = StoragePhotoUtils
                .builder()
                .fileName(photoFilename)
                .type(TypeEntityPhoto.News).build();

        photoStorageService.deletar(storagePhotoUtils);
    }
    public String getFileNamePhoto(News news) {
        return news.getNewsPhoto().getFileName();
    }

}
