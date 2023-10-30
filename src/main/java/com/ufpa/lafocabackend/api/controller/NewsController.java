package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.domain.model.dto.NewsDto;
import com.ufpa.lafocabackend.domain.model.dto.input.NewsInputDto;
import com.ufpa.lafocabackend.domain.service.NewsService;
import com.ufpa.lafocabackend.domain.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public NewsController(NewsService newsService, ModelMapper modelMapper, UserService userService) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<NewsDto> add(@RequestBody NewsInputDto newsInputDto) {

        final News news = modelMapper.map(newsInputDto, News.class);
        news.createSlug();
        final User user = userService.read(newsInputDto.getUserId());
        news.setUser(user);

        final NewsDto newsDto = modelMapper.map(newsService.save(news), NewsDto.class);
        return ResponseEntity.ok(newsDto);
    }
}
