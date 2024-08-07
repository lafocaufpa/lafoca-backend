package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.domain.exception.EntityInUseException;
import com.ufpa.lafocabackend.domain.exception.EntityNotFoundException;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.MemberInfo;
import com.ufpa.lafocabackend.domain.model.dto.input.ArticleInputDto;
import com.ufpa.lafocabackend.repository.ArticleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        if(articleDto.getLineOfResearchIds() != null){
            for (String lineOfResearchId : articleDto.getLineOfResearchIds()) {
                LineOfResearch lineOfResearch = lineOfResearchService.read(lineOfResearchId);
                article.addLineOfResearch(lineOfResearch);
            }
        }

        return articleRepository.save(article);
    }

    public Page<Article> list(String title, String lineOfResearchId, Integer year, Pageable pageable) {
        if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return articleRepository.findByTitleContainingAndLineOfResearchIdAndDate(title, lineOfResearchId, String.valueOf(year), pageable);
        } else if (title != null && !title.isEmpty() && lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return articleRepository.findByTitleContainingAndLineOfResearchId(title, lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty() && year != null) {
            return articleRepository.findByTitleContainingAndDate(title, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty() && year != null) {
            return articleRepository.findByLineOfResearchIdAndDate(lineOfResearchId, String.valueOf(year), pageable);
        } else if (lineOfResearchId != null && !lineOfResearchId.isEmpty()) {
            return articleRepository.findByLineOfResearchId(lineOfResearchId, pageable);
        } else if (title != null && !title.isEmpty()) {
            return articleRepository.findByTitleContaining(title, pageable);
        } else if (year != null) {
            return articleRepository.findByDate(String.valueOf(year), pageable);
        } else {
            return articleRepository.findAll(pageable);
        }
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

        Set<MemberInfo> members = newArticle.getMembers();
        currentArticle.setMembers(members);

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

    private Article getOrFail(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow( () -> new EntityNotFoundException(Article.class.getSimpleName(), articleId));
    }

    public void addLinesOfResearch(Long articleId, List<String> linesOfResearch) {

    }
}
