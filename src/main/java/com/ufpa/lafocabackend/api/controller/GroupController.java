package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.Permission;
import com.ufpa.lafocabackend.domain.model.dto.output.GroupDto;
import com.ufpa.lafocabackend.domain.model.dto.output.PermissionDto;
import com.ufpa.lafocabackend.domain.service.GroupService;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final ModelMapper modelMapper;
    private final GroupService groupService;
    private final PermissionService permissionService;

    public GroupController(ModelMapper modelMapper, GroupService groupService, PermissionService permissionService) {
        this.modelMapper = modelMapper;
        this.groupService = groupService;
        this.permissionService = permissionService;
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PostMapping
    public ResponseEntity<GroupDto> add(@RequestBody @Valid GroupDto groupDto) {

        final Group group = modelMapper.map(groupDto, Group.class);

        final GroupDto groupSaved = modelMapper.map(groupService.save(group), GroupDto.class);

        return ResponseEntity.ok(groupSaved);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> read(@PathVariable Long groupId) {

        final Group group = groupService.read(groupId);
        final GroupDto groupDto = modelMapper.map(group, GroupDto.class);
        return ResponseEntity.ok(groupDto);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping
    public ResponseEntity<Page<GroupDto>> list(@RequestParam(value = "name", required = false) String name, Pageable pageable) {
        Page<Group> list;

        if (name != null && !name.isEmpty()) {
            list = groupService.searchByName(name, pageable);
        } else {
            list = groupService.list(pageable);
        }

        Type listType = new TypeToken<List<GroupDto>>() {
        }.getType();
        List<GroupDto> map = modelMapper.map(list.getContent(), listType);
        PageImpl<GroupDto> groupDtos = new PageImpl<>(map, pageable, list.getTotalElements());

        return LafocaCacheUtil.createCachedResponseGroup(groupDtos);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping("/list")
    public ResponseEntity<List<GroupDto>> listWithoutPagination() {

        List<Group> list = groupService.listWithoutPagination();

        Type listType = new TypeToken<List<GroupDto>>() {

        }.getType();

        final List<GroupDto> groupDtos = modelMapper.map(list, listType);

        return LafocaCacheUtil.createCachedResponseGroup(groupDtos);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> update(@PathVariable Long groupId, @RequestBody GroupDto groupDto) {

        final Group group = groupService.read(groupId);

        modelMapper.map(groupDto, group);

        Set<Permission> permissions = new java.util.HashSet<>(Collections.emptySet());

        for(Long permissionId: groupDto.getPermissionsId()){
            Permission permission = permissionService.read(permissionId);
            permissions.add(permission);
        }


        group.setPermissions(permissions);

        final Group groupUpdated = groupService.update(group);
        final GroupDto groupDtoUpdated = modelMapper.map(groupUpdated, GroupDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable Long groupId) {

        groupService.delete(groupId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @GetMapping("/{groupId}/permissions")
    public ResponseEntity<Collection<PermissionDto>> listPermissions(@PathVariable Long groupId) {

        final Group group = groupService.read(groupId);

        final Set<Permission> permissions = group.getPermissions();

        final Type type = new TypeToken<Set<PermissionDto>>() {

        }.getType();

        final Set<PermissionDto> permissionsDto = modelMapper.map(permissions, type);

        return ResponseEntity.ok(permissionsDto);
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @PutMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> associatePermission(@PathVariable Long groupId,
                                                    @PathVariable Long permissionId) {
        groupService.addPermission(groupId, permissionId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrManagerUsersGroups
    @DeleteMapping("/{groupId}/permissions/{permissionId}")
    public ResponseEntity<Void> disassociatePermission(@PathVariable Long groupId,
                                                       @PathVariable Long permissionId) {
        groupService.removePermission(groupId, permissionId);

        return ResponseEntity.noContent().build();
    }
}







































