package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.PermissionDto;
import com.ufpa.lafocabackend.domain.service.GroupService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final ModelMapper modelMapper;
    private final GroupService groupService;

    public GroupController(ModelMapper modelMapper, GroupService groupService) {
        this.modelMapper = modelMapper;
        this.groupService = groupService;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<GroupDto> add (@RequestBody @Valid GroupDto groupDto) {

        final Group group = modelMapper.map(groupDto, Group.class);

        final GroupDto groupSaved = modelMapper.map(groupService.save(group), GroupDto.class);

        return ResponseEntity.ok(groupSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> read (@PathVariable Long groupId){

        final Group group = groupService.read(groupId);
        final GroupDto groupDto = modelMapper.map(group, GroupDto.class);
        return ResponseEntity.ok(groupDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<Collection<GroupDto>> list (){

        final List<Group> list = groupService.list();

        Type listType = new TypeToken<List<GroupDto>>() {

        }.getType();

        final List<GroupDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> update (@PathVariable Long groupId, @RequestBody GroupDto groupDto){

        final Group group = groupService.read(groupId);

        modelMapper.map(groupDto, group);

        final Group groupUpdated = groupService.update(group);
        final GroupDto groupDtoUpdated = modelMapper.map(groupUpdated, GroupDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete (@PathVariable Long groupId){

        groupService.delete(groupId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{groupId}/permissions")
    public ResponseEntity<Collection<PermissionDto>> listPermissions(@PathVariable Long groupId){

        final Group group = groupService.read(groupId);

        final Set<Permission> permissions = group.getPermissions();

        final Type type = new TypeToken<Set<PermissionDto>>() {

        }.getType();

        final Set<PermissionDto> permissionsDto = modelMapper.map(permissions, type);

        return ResponseEntity.ok(permissionsDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> associatePermission(@PathVariable Long groupId,
                                                  @PathVariable Long permissionId){
        groupService.addPermission(groupId, permissionId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> disassociatePermission(@PathVariable Long groupId,
                                                     @PathVariable Long permissionId){
        groupService.removePermission(groupId, permissionId);

        return ResponseEntity.noContent().build();
    }
}







































