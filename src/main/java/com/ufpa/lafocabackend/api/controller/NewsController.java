package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.NewsPhoto;
import com.ufpa.lafocabackend.domain.model.dto.NewsDto;
import com.ufpa.lafocabackend.domain.model.dto.NewsOutput;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.domain.service.NewsPhotoService;
import com.ufpa.lafocabackend.domain.service.NewsService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{newsSlug}")
    public ResponseEntity<NewsDto> read (@PathVariable String newsSlug ){

        final News news = newsService.read(newsSlug);
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
        final News news = newsService.read(newsSlug);

        modelMapper.map(newsInputDto, news);

        final News newsUpdate = newsService.update(news);
        final NewsDto newsDto = modelMapper.map(newsUpdate, NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }

    @DeleteMapping("/{newsSlug}")
    public ResponseEntity<Void> delete (@PathVariable String newsSlug){

        final News news = newsService.read(newsSlug);

        newsService.delete(news.getSlug());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{newsSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto (MultipartFile photo, @PathVariable String newsSlug) throws IOException {

        final News news = newsService.read(newsSlug);

        NewsPhoto newsPhoto = new NewsPhoto();
        final String originalFileName = LafocaUtils.createPhotoFilename(news.getNewsId(), photo.getOriginalFilename());

        newsPhoto.setNewsPhotoId(news.getNewsId());
        newsPhoto.setFileName(originalFileName);
        newsPhoto.setSize(photo.getSize());
        newsPhoto.setContentType(photo.getContentType());

        final NewsPhoto save = newsPhotoService.save(newsPhoto, photo.getInputStream());

        news.setNewsPhoto(save);
        newsService.save(news);

        final PhotoDto photoDto = modelMapper.map(save, PhotoDto.class);

        return ResponseEntity.ok(photoDto);
    }

    @GetMapping("{newsSlug}/photo")
    public ResponseEntity<?> getPhoto(@PathVariable String newsSlug){
        final News news = newsService.read(newsSlug);
        final NewsPhoto newsPhoto = news.getNewsPhoto();

        if(newsPhoto == null) {
            return ResponseEntity.notFound().build();
        }

        if (newsPhoto.getUrl() != null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, newsPhoto.getUrl()).build();
        } else {

            final PhotoStorageService.RecoveredPhoto recoveredPhoto = newsPhotoService.get(newsPhoto.getFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, newsPhoto.getContentType());

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(recoveredPhoto.getInputStream()));
        }
    }

    @DeleteMapping(value = "{newsSlug}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String newsSlug) {

        final News news = newsService.read(newsSlug);
        newsPhotoService.delete(news.getNewsPhoto().getNewsPhotoId());
        return ResponseEntity.noContent().build();
    }
}
