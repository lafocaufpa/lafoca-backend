package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.dto.input.ArticleInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ArticleDto;
import com.ufpa.lafocabackend.repository.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;
    private final LineOfResearchService lineOfResearchService;

    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, LineOfResearchService lineOfResearchService) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.lineOfResearchService = lineOfResearchService;
    }

    public Article save (ArticleInputDto articleDto) {

        Article article = modelMapper.map(articleDto, Article.class);

        for (String lineOfResearchId : articleDto.getLineOfResearchIds()) {
            LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
            article.addLineOfResearch(lineOfResearch);
        }

        return articleRepository.save(article);
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

    public Article update (Long articleId, ArticleInputDto newArticle) {

        final Article currentArticle = read(articleId);

        modelMapper.map(newArticle, currentArticle);
        currentArticle.setArticleId(articleId);

        List<LineOfResearch> linesOfResearches = new ArrayList<>();
        if(newArticle.getLineOfResearchIds() != null) {
            for (String lineOfResearchId : newArticle.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                linesOfResearches.add(lineOfResearch);
            }
        }

        currentArticle.setLinesOfResearch(linesOfResearches);

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
//
//    public Boolean addLineOfResearchByArticleSlug(String articleSlug, String lineOfResearch){
//        readBySlug(articleSlug).addLineOfResearch(lineOfResearch);
//    }

    private Article getOrFail(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow( () -> new EntityNotFoundException(Article.class.getSimpleName(), articleId));
    }

    public void addLinesOfResearch(Long articleId, List<String> linesOfResearch) {

    }
}
