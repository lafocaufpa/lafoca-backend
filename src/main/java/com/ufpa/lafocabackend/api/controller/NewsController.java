package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.file.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.dto.output.NewsDto;
import com.ufpa.lafocabackend.domain.model.dto.output.NewsOutput;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.domain.service.NewsPhotoService;
import com.ufpa.lafocabackend.domain.service.NewsService;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;
    private final NewsPhotoService newsPhotoService;

    public NewsController(NewsService newsService, ModelMapper modelMapper, NewsPhotoService newsPhotoService) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
        this.newsPhotoService = newsPhotoService;
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
    public ResponseEntity<NewsDto> add (@RequestBody @Valid NewsInputDto newsInputDto) {

        final NewsDto newsDto = modelMapper.map(newsService.save(newsInputDto), NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/read/{newsSlug}")
    public ResponseEntity<NewsDto> readBySlug(@PathVariable String newsSlug ){

        final News news = newsService.readBySlug(newsSlug);
        final NewsDto newsDto = modelMapper.map(news, NewsDto.class);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(newsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDto> read(@PathVariable String newsId ){

        final News news = newsService.read(newsId);
        final NewsDto newsDto = modelMapper.map(news, NewsDto.class);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(newsDto);
    }

    @GetMapping
    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    public ResponseEntity<Page<NewsOutput>> list(
            @RequestParam(value = "title", required = false) String title,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<News> newsPage;

        if (title != null && !title.isEmpty()) {
            newsPage = newsService.searchByTitle(title, pageable);
        } else {
            newsPage = newsService.list(pageable);
        }

        Type listType = new TypeToken<List<NewsOutput>>() {}.getType();
        List<NewsOutput> map = modelMapper.map(newsPage.getContent(), listType);
        Page<NewsOutput> newsOutputs = new PageImpl<>(map, pageable, newsPage.getTotalElements());

        return LafocaCacheUtil.createCachedResponseNews(newsOutputs);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{newsId}")
    public ResponseEntity<NewsDto> update (@PathVariable String newsId, @RequestBody NewsInputDto newsInputDto){
        newsService.read(newsId);


        final News newsUpdate = newsService.update(newsId, newsInputDto);
        final NewsDto newsDto = modelMapper.map(newsUpdate, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/read/{newsSlug}")
    public ResponseEntity<NewsDto> updateBySlug (@PathVariable String newsSlug, @RequestBody NewsInputDto newsInputDto){

        final News newsUpdate = newsService.update(newsSlug, newsInputDto);
        final NewsDto newsDto = modelMapper.map(newsUpdate, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/read/{newsSlug}")
    public ResponseEntity<Void> deleteBySlug(@PathVariable String newsSlug){

        final News news = newsService.readBySlug(newsSlug);

        newsService.delete(news);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete (@PathVariable String newsId){

        final News news = newsService.read(newsId);

        newsService.delete(news);

        return ResponseEntity.noContent().build();
    }


    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PostMapping(value = "/read/{newsSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhotoBySlug(Part file, @PathVariable String newsSlug) throws IOException {

        var filePhoto = new StandardCustomMultipartFile(file);
        final News news = newsService.readBySlug(newsSlug);

        PhotoDto photoDto = newsPhotoService.save(news, filePhoto);
        return ResponseEntity.ok(photoDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PostMapping(value = "/{newsId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto (Part file, @PathVariable String newsId) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);
        final News news = newsService.read(newsId);

        PhotoDto photoDto = newsPhotoService.save(news, customFile);
        return ResponseEntity.ok(photoDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping(value = "/read/{newsSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String newsSlug) {

        final News news = newsService.readBySlug(newsSlug);
        newsPhotoService.delete(news);
        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping(value = "{newsId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String newsId) {

        final News news = newsService.read(newsId);
        newsPhotoService.delete(news);
        return ResponseEntity.noContent().build();
    }
}
