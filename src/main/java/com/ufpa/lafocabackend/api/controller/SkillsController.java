package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.domain.service.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillsController {


    private final SkillService skillService;
    private final ModelMapper modelMapper;

    public SkillsController(SkillService skillService, ModelMapper modelMapper) {
        this.skillService = skillService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<Skill> add (@RequestBody Skill skill) {

        final Skill skillSaved = skillService.save(skill);

        return ResponseEntity.ok(skillSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{skillId}")
    public ResponseEntity<Skill> read (@PathVariable Long skillId){

        final Skill skill = skillService.read(skillId);
        return ResponseEntity.ok(skill);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<Skill>> list (){

        final List<Skill> list = skillService.list();

        return ResponseEntity.ok(list);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{skillsId}")
    public ResponseEntity<Skill> update (@PathVariable Long skillsId, @RequestBody Skill newSkill){

        final Skill currentSkill = skillService.read(skillsId);

        modelMapper.map(newSkill, currentSkill);
        currentSkill.setSkillId(skillsId);

        final Skill skillUpdated = skillService.update(currentSkill);
        return ResponseEntity.ok(skillUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{skillsId}")
    public ResponseEntity<Void> delete (@PathVariable Long skillsId){

        skillService.delete(skillsId);

        return ResponseEntity.noContent().build();
    }
}
