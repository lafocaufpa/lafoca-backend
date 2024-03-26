package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.dto.output.LafocaDto;
import com.ufpa.lafocabackend.domain.service.LafocaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/info")
public class LafocaController {

    private final LafocaService lafocaService;

    public LafocaController(LafocaService lafocaService) {
        this.lafocaService = lafocaService;
    }

    @GetMapping
    public ResponseEntity<List<LafocaDto>> list() {

        final List<LafocaDto> lafoca = lafocaService.list();

        return ResponseEntity.ok(lafoca);
    }

}
