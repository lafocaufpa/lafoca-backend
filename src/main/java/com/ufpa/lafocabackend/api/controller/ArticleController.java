package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.CacheUtil;
import com.ufpa.lafocabackend.domain.model.Article;
import com.ufpa.lafocabackend.domain.model.dto.input.ArticleInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ArticleDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberDto;
import com.ufpa.lafocabackend.domain.service.ArticleService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    public ArticleController(ArticleService articleService, ModelMapper modelMapper) {
        this.articleService = articleService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<ArticleDto> add (@RequestBody @Valid ArticleInputDto articleDto) {

        final ArticleDto articleSaved = modelMapper.map(articleService.save(articleDto), ArticleDto.class);

        return ResponseEntity.ok(articleSaved);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> read (@PathVariable Long articleId){

        final ArticleDto articleDto = modelMapper.map(articleService.read(articleId), ArticleDto.class);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(articleDto);
    }

    @GetMapping("/read/{articleSlug}")
    public ResponseEntity<ArticleDto> readBySlug (@PathVariable String articleSlug){

        final ArticleDto articleDto = modelMapper.map(articleService.readBySlug(articleSlug), ArticleDto.class);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(articleDto);
    }

    @GetMapping
    public ResponseEntity<Page<ArticleDto>> list(@RequestParam(value = "title", required = false) String title, Pageable pageable) {
        Page<Article> list;

        if (title != null && !title.isEmpty()) {
            list = articleService.searchByTitle(title, pageable);
        } else {
            list = articleService.list(pageable);
        }

        Type listType = new TypeToken<List<ArticleDto>>() {}.getType();
        List<ArticleDto> map = modelMapper.map(list.getContent(), listType);
        PageImpl<ArticleDto> articleDtos = new PageImpl<>(map, pageable, list.getTotalElements());

        return CacheUtil.createCachedResponseArticle(articleDtos);
    }


    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleDto> update (@PathVariable Long articleId, @RequestBody ArticleInputDto newArticle){
        
        final ArticleDto articletUpdated = modelMapper.map(articleService.update(articleId, newArticle), ArticleDto.class);
        return ResponseEntity.ok(articletUpdated);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> delete (@PathVariable Long articleId){

        articleService.delete(articleId);

        return ResponseEntity.noContent().build();
    }
}
