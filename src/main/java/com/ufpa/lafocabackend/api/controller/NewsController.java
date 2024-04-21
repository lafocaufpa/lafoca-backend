package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.dto.NewsDto;
import com.ufpa.lafocabackend.domain.model.dto.NewsOutput;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.domain.service.NewsPhotoService;
import com.ufpa.lafocabackend.domain.service.NewsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

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

    @PostMapping
    public ResponseEntity<NewsDto> add (@RequestBody NewsInputDto newsInputDto) {

        final News news = modelMapper.map(newsInputDto, News.class);

        final NewsDto newsDto = modelMapper.map(newsService.save(news), NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @GetMapping("/search/{newsSlug}")
    public ResponseEntity<NewsDto> readBySlug(@PathVariable String newsSlug ){

        final News news = newsService.readBySlug(newsSlug);
        final NewsDto newsDto = modelMapper.map(news, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsDto> read(@PathVariable String newsId ){

        final News news = newsService.read(newsId);
        final NewsDto newsDto = modelMapper.map(news, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @GetMapping
    public ResponseEntity<Collection<NewsOutput>> list (){

        final List<News> list = newsService.list();

        Type listType = new TypeToken<List<NewsOutput>>() {}.getType();

        final List<NewsOutput> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @PutMapping("/{newsSlug}")
    public ResponseEntity<NewsDto> update (@PathVariable String newsSlug, @RequestBody NewsInputDto newsInputDto){
        final News news = newsService.readBySlug(newsSlug);

        modelMapper.map(newsInputDto, news);

        final News newsUpdate = newsService.update(news);
        final NewsDto newsDto = modelMapper.map(newsUpdate, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @DeleteMapping("/{newsSlug}")
    public ResponseEntity<Void> delete (@PathVariable String newsSlug){

        final News news = newsService.readBySlug(newsSlug);

        newsService.delete(news.getSlug());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/search/{newsSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhotoBySlug(MultipartFile photo, @PathVariable String newsSlug) throws IOException {

        final News news = newsService.readBySlug(newsSlug);

        PhotoDto photoDto = newsPhotoService.save(news, photo);
        return ResponseEntity.ok(photoDto);
    }

    @PostMapping(value = "/{newsId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto (MultipartFile photo, @PathVariable String newsId) throws IOException {

        final News news = newsService.read(newsId);

        PhotoDto photoDto = newsPhotoService.save(news, photo);
        return ResponseEntity.ok(photoDto);
    }

    @DeleteMapping(value = "/search/{newsSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String newsSlug) {

        final News news = newsService.readBySlug(newsSlug);
        newsPhotoService.delete(news);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "{newsId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String newsId) {

        final News news = newsService.read(newsId);
        newsPhotoService.delete(news);
        return ResponseEntity.noContent().build();
    }
}
