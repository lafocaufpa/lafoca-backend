package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.TccInputDto;
import com.ufpa.lafocabackend.domain.service.TccService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/tccs")
public class TccController {

    private final TccService tccService;
    private final ModelMapper modelMapper;

    public TccController(TccService tccService, ModelMapper modelMapper) {
        this.tccService = tccService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
    public ResponseEntity<TccInputDto> add (@RequestBody @Valid TccInputDto tccInputDto) {


        final Tcc tccSaved = tccService.save(tccInputDto);

        final TccInputDto map = modelMapper.map(tccSaved, TccInputDto.class);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{tccId}")
    public ResponseEntity<TccInputDto> read (@PathVariable Long tccId){

        final Tcc tcc = tccService.read(tccId);

        final TccInputDto map = modelMapper.map(tcc, TccInputDto.class);

        return LafocaCacheUtil.createCachedResponseTcc(map);
    }

    @GetMapping
    public ResponseEntity<Page<TccInputDto>> list(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "lineOfResearchId", required = false) String lineOfResearchId,
            @RequestParam(value = "year", required = false) Integer year,
            Pageable pageable) {

        Page<Tcc> tccList = tccService.list(title, lineOfResearchId, year, pageable);

        Type listType = new TypeToken<List<TccInputDto>>() {}.getType();
        List<TccInputDto> tccInputDtoList = modelMapper.map(tccList.getContent(), listType);
        Page<TccInputDto> tccInputDtos = new PageImpl<>(tccInputDtoList, pageable, tccList.getTotalElements());

        return LafocaCacheUtil.createCachedResponseTcc(tccInputDtos);
    }


    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{tccId}")
    public ResponseEntity<TccInputDto> update (@PathVariable Long tccId, @RequestBody TccInputDto newTcc){

        final Tcc tccUpdated = tccService.update(tccId, newTcc);
        final TccInputDto tccInputDto = modelMapper.map(tccUpdated, TccInputDto.class);
        return ResponseEntity.ok(tccInputDto);
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{tccId}")
    public ResponseEntity<Void> delete (@PathVariable Long tccId){

        tccService.delete(tccId);

        return ResponseEntity.noContent().build();
    }
}
