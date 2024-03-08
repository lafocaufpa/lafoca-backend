package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Skills;
import com.ufpa.lafocabackend.domain.service.SkillsService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillsController {


    private final SkillsService skillsService;
    private final ModelMapper modelMapper;

    public SkillsController(SkillsService skillsService, ModelMapper modelMapper) {
        this.skillsService = skillsService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<Skills> add (@RequestBody Skills skills) {

        final Skills skillSaved = skillsService.save(skills);

        return ResponseEntity.ok(skillSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{skillId}")
    public ResponseEntity<Skills> read (@PathVariable Long skillId){

        final Skills skill = skillsService.read(skillId);
        return ResponseEntity.ok(skill);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<Skills>> list (){

        final List<Skills> list = skillsService.list();

        return ResponseEntity.ok(list);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{skillsId}")
    public ResponseEntity<Skills> update (@PathVariable Long skillsId, @RequestBody Skills newSkill){

        final Skills currentSkill = skillsService.read(skillsId);

        modelMapper.map(newSkill, currentSkill);
        currentSkill.setId(skillsId);

        final Skills skillUpdated = skillsService.update(currentSkill);
        return ResponseEntity.ok(skillUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{skillsId}")
    public ResponseEntity<Void> delete (@PathVariable Long skillsId){

        skillsService.delete(skillsId);

        return ResponseEntity.noContent().build();
    }
}
