package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.dto.ArticleDto;
import com.ufpa.lafocabackend.domain.service.ArticleService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    public ArticleController(ArticleService articleService, ModelMapper modelMapper) {
        this.articleService = articleService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<ArticleDto> add (@RequestBody ArticleDto articleDto) {

        final ArticleDto articleSaved = modelMapper.map(articleService.save(articleDto), ArticleDto.class);

        return ResponseEntity.ok(articleSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> read (@PathVariable Long articleId){

        final ArticleDto articleDto = modelMapper.map(articleService.read(articleId), ArticleDto.class);
        return ResponseEntity.ok(articleDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<ArticleDto>> list (){

        final List<Article> list = articleService.list();

        Type listType = new TypeToken<List<ArticleDto>>() {

        }.getType();

        final List<ArticleDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleDto> update (@PathVariable Long articleId, @RequestBody ArticleDto newArticle){
        
        final ArticleDto articletUpdated = modelMapper.map(articleService.update(articleId, newArticle), ArticleDto.class);
        return ResponseEntity.ok(articletUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> delete (@PathVariable Long articleId){

        articleService.delete(articleId);

        return ResponseEntity.noContent().build();
    }
}
