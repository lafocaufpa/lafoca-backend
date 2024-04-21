package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.dto.ArticleDto;
import com.ufpa.lafocabackend.repository.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;

    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }

    public Article save (ArticleDto articleDto) {

        return articleRepository.save(modelMapper.map(articleDto, Article.class));
    }

    public List<Article> list (){

        return articleRepository.findAll();
    }

    public Article read (Long articleId) {
        return getOrFail(articleId);
    }

    public Article readBySlug(String slug) {
        return articleRepository.findBySlug(slug).
                orElseThrow(() -> new EntityNotFoundException(Article.class.getSimpleName(), slug));
    }

    public Article update (Long articleId, ArticleDto newArticle) {

        final Article currentArticle = read(articleId);

        modelMapper.map(newArticle, currentArticle);
        currentArticle.setArticleId(articleId);

        return articleRepository.save(currentArticle);
    }

    public void delete (Long articleId) {

        try {
            articleRepository.deleteById(articleId);
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(Article.class.getSimpleName(), articleId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(Article.class.getSimpleName(), articleId);
        }

    }

    private Article getOrFail(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow( () -> new EntityNotFoundException(Article.class.getSimpleName(), articleId));
    }
}
