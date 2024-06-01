package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.dto.output.LafocaDto;
import com.ufpa.lafocabackend.domain.service.LafocaService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/info")
public class LafocaController {

    private final LafocaService lafocaService;

    public LafocaController(LafocaService lafocaService) {
        this.lafocaService = lafocaService;
    }

    @GetMapping
    public ResponseEntity<LafocaDto> read() {
        final LafocaDto lafocaDto = lafocaService.printCounts();
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(lafocaDto);
    }
}
