package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.FunctionStudent;
import com.ufpa.lafocabackend.domain.model.dto.FunctionStudentDto;
import com.ufpa.lafocabackend.domain.service.FunctionStudentService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/function-student")
public class FunctionStudentController {

    private final FunctionStudentService functionStudentService;
    private final ModelMapper modelMapper;

    public FunctionStudentController(FunctionStudentService functionStudentService, ModelMapper modelMapper) {
        this.functionStudentService = functionStudentService;
        this.modelMapper = modelMapper;
    }


    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<FunctionStudentDto> add (@RequestBody FunctionStudentDto functionStudentDto) {

        final FunctionStudent functionStudent = modelMapper.map(functionStudentDto, FunctionStudent.class);

        final FunctionStudentDto functionStudentSaved = modelMapper.map(functionStudentService.save(functionStudent), FunctionStudentDto.class);

        return ResponseEntity.ok(functionStudentSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{functionStudentId}")
    public ResponseEntity<FunctionStudentDto> read (@PathVariable Long functionStudentId){

        final FunctionStudent functionStudent = functionStudentService.read(functionStudentId);
        final FunctionStudentDto functionStudentDto = modelMapper.map(functionStudent, FunctionStudentDto.class);
        return ResponseEntity.ok(functionStudentDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<FunctionStudentDto>> list (){

        final List<FunctionStudent> list = functionStudentService.list();

        Type listType = new TypeToken<List<FunctionStudentDto>>() {

        }.getType();

        final List<FunctionStudentDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{functionStudentId}")
    public ResponseEntity<FunctionStudentDto> update (@PathVariable Long functionStudentId, @RequestBody FunctionStudentDto functionStudentDto){

        final FunctionStudent functionStudent = functionStudentService.read(functionStudentId);

        modelMapper.map(functionStudentDto, functionStudent);

        final FunctionStudent functionStudentUpdated = functionStudentService.update(functionStudent);
        final FunctionStudentDto groupDtoUpdated = modelMapper.map(functionStudentUpdated, FunctionStudentDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{functionStudentId}")
    public ResponseEntity<Void> delete (@PathVariable Long functionStudentId){

        functionStudentService.delete(functionStudentId);

        return ResponseEntity.noContent().build();
    }
}
