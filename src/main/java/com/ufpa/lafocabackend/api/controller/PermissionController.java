package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.dto.PermissionDto;
import com.ufpa.lafocabackend.domain.service.PermissionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {


    private final PermissionService permissionService;
    private final ModelMapper modelMapper;

    public PermissionController(PermissionService permissionService, ModelMapper modelMapper) {
        this.permissionService = permissionService;
        this.modelMapper = modelMapper;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<PermissionDto> add (@RequestBody @Valid PermissionDto permissionDto) {

        final Permission permission = modelMapper.map(permissionDto, Permission.class);

        final PermissionDto permissionSaved = modelMapper.map(permissionService.save(permission), PermissionDto.class);

        return ResponseEntity.ok(permissionSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> read (@PathVariable Long permissionId ){

        final Permission permission = permissionService.read(permissionId);
        final PermissionDto permissionDto = modelMapper.map(permission, PermissionDto.class);
        return ResponseEntity.ok(permissionDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<Collection<PermissionDto>> list (){

        final List<Permission> list = permissionService.list();

        Type listType = new TypeToken<List<PermissionDto>>() {

        }.getType();

        final List<PermissionDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> update (@PathVariable Long permissionId, @RequestBody PermissionDto permissionDto){
        final Permission permission = permissionService.read(permissionId);

        modelMapper.map(permissionDto, permission);

        final Permission PermissionUpdate = permissionService.update(permission);
        final PermissionDto permissionDtoUpdate = modelMapper.map(PermissionUpdate, PermissionDto.class);
        return ResponseEntity.ok(permissionDtoUpdate);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> delete (@PathVariable Long permissionId){

        permissionService.delete(permissionId);

        return ResponseEntity.noContent().build();
    }
}
