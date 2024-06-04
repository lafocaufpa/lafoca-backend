package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.NewsPhotoRepository;
import com.ufpa.lafocabackend.repository.NewsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {


    private final NewsRepository newsRepository;
    private final ModelMapper modelMapper;
    private final LineOfResearchService lineOfResearchService;
    private final NewsPhotoRepository newsPhotoRepository;
    private final PhotoStorageService photoStorageService;

    public NewsService(NewsRepository newsRepository, ModelMapper modelMapper, LineOfResearchService lineOfResearchService, NewsPhotoRepository newsPhotoRepository, PhotoStorageService photoStorageService) {
        this.newsRepository = newsRepository;
        this.modelMapper = modelMapper;
        this.lineOfResearchService = lineOfResearchService;
        this.newsPhotoRepository = newsPhotoRepository;
        this.photoStorageService = photoStorageService;
    }
    
    public News save(NewsInputDto newsInputDto){

        final News news = modelMapper.map(newsInputDto, News.class);

        for (String lineOfResearchId : newsInputDto.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
            news.addLineOfResearch(lineOfResearch);
        }

        return newsRepository.save(news);
    }

    public List<News> list(){
        return newsRepository.findAll();
    }

    @Transactional
    public News update(String newsSlug, NewsInputDto newNewsInputDto){

        News news = readBySlug(newsSlug);
        modelMapper.map(newNewsInputDto, news);
        news.setNewsId(newsSlug);

        List<LineOfResearch> linesOfResearches = new ArrayList<>();
        for (String lineOfResearchId : newNewsInputDto.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
            linesOfResearches.add(lineOfResearch);
        }

        news.setLinesOfResearch(linesOfResearches);

        return newsRepository.save(news);
    }

    @Transactional
    public void delete(News news){

        String photoFilename = newsPhotoRepository.findNewsPhotoFileNameByPhotoId(news.getNewsId());

        var storagePhotoUtils = StoragePhotoUtils
                .builder()
                .fileName(photoFilename)
                .type(TypeEntityPhoto.News).build();



        try {
            newsRepository.deleteBySlug(news.getSlug());
            newsRepository.flush();
            photoStorageService.deletar(storagePhotoUtils);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(News.class.getSimpleName(), news.getSlug());
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(News.class.getSimpleName(), news.getSlug());
        }

    }

    private News getOrFail(String newsId) {
        return newsRepository
                .findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException(News.class.getSimpleName(), newsId));
    }

    public News readBySlug(String newsSlug) {
        return newsRepository
                .findBySlug(newsSlug)
                .orElseThrow(() -> new EntityNotFoundException(News.class.getSimpleName(), newsSlug));
    }

    public News read(String newsId) {
        return getOrFail(newsId);
    }

}
