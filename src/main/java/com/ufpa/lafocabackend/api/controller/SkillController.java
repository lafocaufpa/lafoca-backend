package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.dto.output.SkillDto;
import com.ufpa.lafocabackend.domain.service.SkillService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<SkillDto> add (@RequestBody @Valid SkillDto skillDto) {

        final SkillDto skillSaved = skillService.save(skillDto);

        return ResponseEntity.ok(skillSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDto> read (@PathVariable Integer skillId){

        return ResponseEntity.ok(skillService.read(skillId));
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<Page<SkillDto>> list (Pageable pageable){

        Page<SkillDto> list = skillService.list(pageable);


        return ResponseEntity.ok(list);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{skillId}")
    public ResponseEntity<SkillDto> update (@PathVariable Integer skillId, @RequestBody SkillDto newSkill){


        return ResponseEntity.ok(skillService.update(skillId, newSkill));
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> delete (@PathVariable Integer skillId){

        skillService.delete(skillId);

        return ResponseEntity.noContent().build();
    }
}
