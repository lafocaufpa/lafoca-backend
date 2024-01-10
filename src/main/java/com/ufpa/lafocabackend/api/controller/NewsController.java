package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.LafocaSecurity;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.Photo;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.NewsDto;
import com.ufpa.lafocabackend.domain.model.dto.NewsOutput;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.domain.model.dto.input.PhotoInputDto;
import com.ufpa.lafocabackend.domain.service.NewsService;
import com.ufpa.lafocabackend.domain.service.PhotoService;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService;
import com.ufpa.lafocabackend.domain.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserService userService;
    private final PhotoService photoService;
    private final LafocaSecurity lafocaSecurity;

    public NewsController(NewsService newsService, ModelMapper modelMapper, UserService userService, PhotoService photoService, LafocaSecurity lafocaSecurity) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.photoService = photoService;
        this.lafocaSecurity = lafocaSecurity;
    }

    @PostMapping
    public ResponseEntity<NewsDto> add (@RequestBody NewsInputDto newsInputDto) {

        final News news = modelMapper.map(newsInputDto, News.class);
        news.createSlug();
        news.createDescription();

        final User user = userService.read(lafocaSecurity.getUserId());
        news.setUser(user);

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

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{newsSlug}")
    public ResponseEntity<Void> delete (@PathVariable String newsSlug){

        final News news = newsService.read(newsSlug);

        newsService.delete(news.getSlug());

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{newsSlug}/news-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto (PhotoInputDto photoInputDto, @PathVariable String newsSlug) throws IOException {

        final News news = newsService.read(newsSlug);
        final Photo photo = new Photo();
        final MultipartFile multipartFile = photoInputDto.getPhoto();

        final String originalFileName = news.getSlug() + (multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf("."));

        photo.setPhotoId(news.getSlug());
        photo.setFileName(originalFileName);
        photo.setSize(multipartFile.getSize());
        photo.setContentType(multipartFile.getContentType());

        final Photo save = photoService.save(photo, multipartFile.getInputStream());
        final PhotoDto photoDto = modelMapper.map(save, PhotoDto.class);

        return ResponseEntity.ok(photoDto);
    }

    @GetMapping("{newsSlug}/news-photo")
    public ResponseEntity<?> getPhoto(@PathVariable String newsSlug){
        final News news = newsService.read(newsSlug);

        final PhotoStorageService.RecoveredPhoto recoveredPhoto = photoService.get(news.getSlug());

        if (recoveredPhoto.hasUrl()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, recoveredPhoto
                            .getUrl()).build();
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, recoveredPhoto.getContentType());

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(recoveredPhoto.getInputStream()));
        }
    }
}
