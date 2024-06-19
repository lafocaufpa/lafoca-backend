package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.dto.YearClassDTO;
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

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<YearClassDTO> add(@RequestBody @Valid YearClassDTO yearClassDto) {
        final YearClassDTO yearClassSaved = yearClassService.save(yearClassDto);
        return ResponseEntity.ok(yearClassSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{yearClassId}")
    public ResponseEntity<YearClassDTO> read(@PathVariable Long yearClassId) {
        return ResponseEntity.ok(yearClassService.read(yearClassId));
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<Page<YearClassDTO>> list(Pageable pageable) {
        Page<YearClassDTO> list = yearClassService.list(pageable);
        return ResponseEntity.ok(list);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/list")
    public ResponseEntity<List<YearClassDTO>> listWithoutPage() {
        List<YearClassDTO> list = yearClassService.listWithoutPagination();
        return ResponseEntity.ok(list);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{yearClassId}")
    public ResponseEntity<YearClassDTO> update(@PathVariable Long yearClassId, @RequestBody YearClassDTO newYearClass) {
        return ResponseEntity.ok(yearClassService.update(yearClassId, newYearClass));
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{yearClassId}")
    public ResponseEntity<Void> delete(@PathVariable Long yearClassId) {
        yearClassService.delete(yearClassId);
        return ResponseEntity.noContent().build();
    }
}
