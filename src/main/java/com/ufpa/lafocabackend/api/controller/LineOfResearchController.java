package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.LineOfResearch;
import com.ufpa.lafocabackend.domain.model.dto.output.LineOfResearchDto;
import com.ufpa.lafocabackend.domain.service.LineOfResearchService;
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
    @RequestMapping("/lines-of-research")

public class LineOfResearchController {

    private final LineOfResearchService lineOfResearchService;
    private final ModelMapper modelMapper;

    public LineOfResearchController(LineOfResearchService lineOfResearchService, ModelMapper modelMapper) {
        this.lineOfResearchService = lineOfResearchService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
    public ResponseEntity<LineOfResearchDto> add (@RequestBody @Valid LineOfResearchDto lineOfResearchDto) {

        final LineOfResearchDto lineOfResearchSaved = modelMapper.map(lineOfResearchService.save(lineOfResearchDto), LineOfResearchDto.class);

        return ResponseEntity.ok(lineOfResearchSaved);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/{lineOfResearchId}")
    public ResponseEntity<LineOfResearchDto> read (@PathVariable String lineOfResearchId){

        final LineOfResearchDto lineOfResearchDto = modelMapper.map(lineOfResearchService.read(lineOfResearchId), LineOfResearchDto.class);
        return ResponseEntity.ok(lineOfResearchDto);
    }

    @GetMapping
    public ResponseEntity<Page<LineOfResearchDto>> list(@RequestParam(value = "name", required = false) String name, Pageable pageable) {
        Page<LineOfResearch> list;

        if (name != null && !name.isEmpty()) {
            list = lineOfResearchService.searchByName(name, pageable);
        } else {
            list = lineOfResearchService.list(pageable);
        }

        Type listType = new TypeToken<List<LineOfResearchDto>>() {}.getType();
        List<LineOfResearchDto> map = modelMapper.map(list.getContent(), listType);
        PageImpl<LineOfResearchDto> lineOfResearchDtos = new PageImpl<>(map, pageable, list.getTotalElements());

        return LafocaCacheUtil.createCachedResponseLineOfResearch(lineOfResearchDtos);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{lineOfResearchId}")
    public ResponseEntity<LineOfResearchDto> update (@PathVariable String lineOfResearchId, @RequestBody @Valid LineOfResearchDto newArticle){
        
        final LineOfResearchDto articletUpdated = modelMapper.map(lineOfResearchService.update(lineOfResearchId, newArticle), LineOfResearchDto.class);
        return ResponseEntity.ok(articletUpdated);
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{lineOfResearchId}")
    public ResponseEntity<Void> delete (@PathVariable String lineOfResearchId){

        lineOfResearchService.delete(lineOfResearchId);

        return ResponseEntity.noContent().build();
    }
}
