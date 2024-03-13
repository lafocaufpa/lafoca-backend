package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Tcc;
import com.ufpa.lafocabackend.domain.model.dto.input.TccDto;
import com.ufpa.lafocabackend.domain.service.StudentService;
import com.ufpa.lafocabackend.domain.service.TccService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/tccs")
public class TccController {


    private final TccService tccService;
    private final ModelMapper modelMapper;
    private final StudentService studentService;

    public TccController(TccService tccService, ModelMapper modelMapper, StudentService studentService) {
        this.tccService = tccService;
        this.modelMapper = modelMapper;
        this.studentService = studentService;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<TccDto> add (@RequestBody TccDto tccDto) {

        final Tcc tcc = modelMapper.map(tccDto, Tcc.class);

        final Tcc tccSaved = tccService.save(tcc);
        final TccDto map = modelMapper.map(tccSaved, TccDto.class);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{tccId}")
    public ResponseEntity<TccDto> read (@PathVariable Long tccId){

        final Tcc tcc = tccService.read(tccId);

        final TccDto map = modelMapper.map(tcc, TccDto.class);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<TccDto>> list (){

        final List<Tcc> list = tccService.list();

        final Type type = new TypeToken<List<TccDto>>() {
        }.getType();

        final List<TccDto> map = modelMapper.map(list, type);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{tccId}")
    public ResponseEntity<TccDto> update (@PathVariable Long tccId, @RequestBody TccDto newTcc){

        final Tcc currentTcc = tccService.read(tccId);
        studentService.read(newTcc.getStudentId());
        modelMapper.map(newTcc, currentTcc);
        currentTcc.setTccId(tccId);

        final Tcc tccUpdated = tccService.update(currentTcc);
        final TccDto tccDto = modelMapper.map(tccUpdated, TccDto.class);
        return ResponseEntity.ok(tccDto);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{tccId}")
    public ResponseEntity<Void> delete (@PathVariable Long tccId){

        tccService.delete(tccId);

        return ResponseEntity.noContent().build();
    }
}
