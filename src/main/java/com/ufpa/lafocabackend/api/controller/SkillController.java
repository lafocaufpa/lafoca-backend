package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.file.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.output.SkillDto;
import com.ufpa.lafocabackend.domain.service.SkillPictureService;
import com.ufpa.lafocabackend.domain.service.SkillService;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;
    private final SkillPictureService skillPictureService;

    public SkillController(SkillService skillService, SkillPictureService skillPictureService) {
        this.skillService = skillService;
        this.skillPictureService = skillPictureService;
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
    public ResponseEntity<SkillDto> add (@RequestBody @Valid SkillDto skillDto) {

        final SkillDto skillSaved = skillService.save(skillDto);

        return ResponseEntity.ok(skillSaved);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/{skillId}")
    public ResponseEntity<SkillDto> read (@PathVariable Long skillId){

        return ResponseEntity.ok(skillService.read(skillId));
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping
    public ResponseEntity<Page<SkillDto>> list(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable) {

        Page<SkillDto> skillDtos = skillService.list(name, pageable);
        return LafocaCacheUtil.createCachedResponseSkill(skillDtos);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{skillId}")
    public ResponseEntity<SkillDto> update (@PathVariable Long skillId, @RequestBody SkillDto newSkill){


        return ResponseEntity.ok(skillService.update(skillId, newSkill));
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> delete (@PathVariable Long skillId){

        skillService.delete(skillId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PostMapping(value = "/{skillId}/pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<PhotoDto> addPic(Part file, @PathVariable Long skillId) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);

        Skill skill = skillService.getOrFail(skillId);

        return ResponseEntity.ok(skillPictureService.save(skill, customFile));
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping(value = "/{skillId}/pic")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long skillId) {

        Skill skill = skillService.getOrFail(skillId);

        if(skill.getSkillPicture() != null){
            skillPictureService.delete(skill);
        }

        return ResponseEntity.noContent().build();
    }
    
}
