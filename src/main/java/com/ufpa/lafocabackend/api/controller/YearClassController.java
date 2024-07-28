package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.dto.YearClassDTO;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.domain.service.YearClassService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/year-classes")
public class YearClassController {

    private final YearClassService yearClassService;

    public YearClassController(YearClassService yearClassService) {
        this.yearClassService = yearClassService;
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
    public ResponseEntity<YearClassDTO> add(@RequestBody @Valid YearClassDTO yearClassDto) {
        final YearClassDTO yearClassSaved = yearClassService.save(yearClassDto);
        return ResponseEntity.ok(yearClassSaved);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/{yearClassId}")
    public ResponseEntity<YearClassDTO> read(@PathVariable Long yearClassId) {
        return ResponseEntity.ok(yearClassService.read(yearClassId));
    }

    @GetMapping
    public ResponseEntity<Page<YearClassDTO>> list(Pageable pageable) {
        Page<YearClassDTO> list = yearClassService.list(pageable);
        return LafocaCacheUtil.createCachedResponseClasses(list);
    }

    @GetMapping("/{yearClassId}/members")
    public ResponseEntity<Page<MemberResumed>> listMembersByYearClass(
            @PathVariable Long yearClassId,
            @RequestParam(value = "fullName", required = false) String name,
            Pageable pageable) {

        Page<MemberResumed> members = yearClassService.listMembersByYearClass(yearClassId, name, pageable);
        return LafocaCacheUtil.createCachedResponseClasses(members);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @GetMapping("/list")
    public ResponseEntity<List<YearClassDTO>> listWithoutPage() {
        List<YearClassDTO> list = yearClassService.listWithoutPagination();
        return LafocaCacheUtil.createCachedResponseClasses(list);
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{yearClassId}")
    public ResponseEntity<YearClassDTO> update(@PathVariable Long yearClassId, @RequestBody YearClassDTO newYearClass) {
        return ResponseEntity.ok(yearClassService.update(yearClassId, newYearClass));
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{yearClassId}")
    public ResponseEntity<Void> delete(@PathVariable Long yearClassId) {
        yearClassService.delete(yearClassId);
        return ResponseEntity.noContent().build();
    }
}
