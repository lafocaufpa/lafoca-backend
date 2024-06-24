package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.CacheUtil;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.ArticleDto;
import com.ufpa.lafocabackend.domain.service.TccService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/tccs")
public class TccController {

    private final TccService tccService;
    private final ModelMapper modelMapper;

    public TccController(TccService tccService, ModelMapper modelMapper) {
        this.tccService = tccService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<TccInputDto> add (@RequestBody @Valid TccInputDto tccInputDto) {

        final Tcc tcc = modelMapper.map(tccInputDto, Tcc.class);

        final Tcc tccSaved = tccService.save(tcc);
        final TccInputDto map = modelMapper.map(tccSaved, TccInputDto.class);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{tccId}")
    public ResponseEntity<TccInputDto> read (@PathVariable Long tccId){

        final Tcc tcc = tccService.read(tccId);

        final TccInputDto map = modelMapper.map(tcc, TccInputDto.class);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS)).body(map);
    }

    @GetMapping
    public ResponseEntity<Page<TccInputDto>> list(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable) {

        Page<Tcc> tccList = tccService.list(name, pageable);

        Type listType = new TypeToken<List<TccInputDto>>() {}.getType();
        List<TccInputDto> tccInputDtoList = modelMapper.map(tccList.getContent(), listType);
        Page<TccInputDto> tccInputDtos = new PageImpl<>(tccInputDtoList, pageable, tccList.getTotalElements());

        return CacheUtil.createCachedResponseTcc(tccInputDtos);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{tccId}")
    public ResponseEntity<TccInputDto> update (@PathVariable Long tccId, @RequestBody TccInputDto newTcc){

        final Tcc currentTcc = tccService.read(tccId);
        modelMapper.map(newTcc, currentTcc);
        currentTcc.setTccId(tccId);

        final Tcc tccUpdated = tccService.update(currentTcc);
        final TccInputDto tccInputDto = modelMapper.map(tccUpdated, TccInputDto.class);
        return ResponseEntity.ok(tccInputDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{tccId}")
    public ResponseEntity<Void> delete (@PathVariable Long tccId){

        tccService.delete(tccId);

        return ResponseEntity.noContent().build();
    }
}
