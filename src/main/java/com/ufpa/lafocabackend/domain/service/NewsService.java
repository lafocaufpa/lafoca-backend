package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.repository.NewsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NewsService {


    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    
    public News save(News news){
        

        return newsRepository.save(news);
    }

    public List<News> list(){
        return newsRepository.findAll();
    }

    @Transactional
    public News update(News news){
        return save(news);
    }

    @Transactional
    public void delete(String newsId){

        try {
            newsRepository.deleteBySlug(newsId);
            newsRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(getClass().getSimpleName(), newsId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(getClass().getSimpleName(), newsId);
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
