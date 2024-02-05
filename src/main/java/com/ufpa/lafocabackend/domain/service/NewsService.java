package com.ufpa.lafocabackend.domain.service;

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
            throw new RuntimeException("Entity in use: id " + newsId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("News not found: id " + newsId);
        }

    }

    private News getOrFail(String newsSlug) {
        return newsRepository.findBySlug(newsSlug).get();
    }

    public News read(String newsSlug) {
        newsExists(newsSlug);
        return getOrFail(newsSlug);
    }

    public void newsExists(String newsSlug){

        if(!newsRepository.existsByNewsSlug(newsSlug)){
            throw new RuntimeException("User not found: id " + newsSlug);
        }
    }
}
