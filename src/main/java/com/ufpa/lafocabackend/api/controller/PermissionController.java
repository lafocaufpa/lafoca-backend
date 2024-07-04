package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.dto.output.PermissionDto;
import com.ufpa.lafocabackend.domain.service.PermissionService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
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

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PostMapping
    public ResponseEntity<PermissionDto> add (@RequestBody @Valid PermissionDto permissionDto) {

        final Permission permission = modelMapper.map(permissionDto, Permission.class);

        final PermissionDto permissionSaved = modelMapper.map(permissionService.save(permission), PermissionDto.class);

        return ResponseEntity.ok(permissionSaved);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> read (@PathVariable Long permissionId ){

        final Permission permission = permissionService.read(permissionId);
        final PermissionDto permissionDto = modelMapper.map(permission, PermissionDto.class);
        return ResponseEntity.ok(permissionDto);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping
    public ResponseEntity<Page<PermissionDto>> list(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable) {

        Page<Permission> permissionPage;

        if (name != null && !name.isEmpty()) {
            permissionPage = permissionService.searchByName(name, pageable);
        } else {
            permissionPage = permissionService.list(pageable);
        }

        Type listType = new TypeToken<List<PermissionDto>>() {}.getType();
        List<PermissionDto> map = modelMapper.map(permissionPage.getContent(), listType);
        Page<PermissionDto> permissionDtos = new PageImpl<>(map, pageable, permissionPage.getTotalElements());

        return LafocaCacheUtil.createCachedResponsePermission(permissionDtos);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionDto> update (@PathVariable Long permissionId, @RequestBody PermissionDto permissionDto){
        final Permission permission = permissionService.read(permissionId);

        modelMapper.map(permissionDto, permission);

        final Permission PermissionUpdate = permissionService.update(permission);
        final PermissionDto permissionDtoUpdate = modelMapper.map(PermissionUpdate, PermissionDto.class);
        return ResponseEntity.ok(permissionDtoUpdate);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> delete (@PathVariable Long permissionId){

        permissionService.delete(permissionId);

        return ResponseEntity.noContent().build();
    }
}
