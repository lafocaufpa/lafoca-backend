package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.utils.CacheUtil;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping
public class HealthCheck {

    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() {
        return CacheUtil.createCachedResponse("<h1>Hello World</h1>", 120);
    }
}
