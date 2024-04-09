package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.dto.SkillDto;
import com.ufpa.lafocabackend.domain.service.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService, ModelMapper modelMapper) {
        this.skillService = skillService;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<SkillDto> add (@RequestBody SkillDto skillDto) {

        final SkillDto skillSaved = skillService.save(skillDto);

        return ResponseEntity.ok(skillSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDto> read (@PathVariable Long skillId){

        return ResponseEntity.ok(skillService.read(skillId));
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<SkillDto>> list (){

        return ResponseEntity.ok(skillService.list());
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{skillId}")
    public ResponseEntity<SkillDto> update (@PathVariable Long skillId, @RequestBody SkillDto newSkill){


        return ResponseEntity.ok(skillService.update(skillId, newSkill));
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> delete (@PathVariable Long skillId){

        skillService.delete(skillId);

        return ResponseEntity.noContent().build();
    }
}
