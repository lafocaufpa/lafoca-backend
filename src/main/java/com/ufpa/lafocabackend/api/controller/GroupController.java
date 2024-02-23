package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.domain.model.Group;
import com.ufpa.lafocabackend.domain.model.dto.GroupDto;
import com.ufpa.lafocabackend.domain.service.GroupService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final ModelMapper modelMapper;
    private final GroupService groupService;

    public GroupController(ModelMapper modelMapper, GroupService groupService) {
        this.modelMapper = modelMapper;
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<GroupDto> add (@RequestBody GroupDto groupDto) {

        final Group group = modelMapper.map(groupDto, Group.class);

        final GroupDto groupSaved = modelMapper.map(groupService.save(group), GroupDto.class);

        return ResponseEntity.ok(groupSaved);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> read (@PathVariable Long groupId){

        final Group group = groupService.read(groupId);
        final GroupDto groupDto = modelMapper.map(group, GroupDto.class);
        return ResponseEntity.ok(groupDto);
    }

    @GetMapping
    public ResponseEntity<Collection<GroupDto>> list (){

        final List<Group> list = groupService.list();

        Type listType = new TypeToken<List<GroupDto>>() {

        }.getType();

        final List<GroupDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> update (@PathVariable Long groupId, @RequestBody GroupDto groupDto){

        final Group group = groupService.read(groupId);

        modelMapper.map(groupDto, group);

        final Group groupUpdated = groupService.update(group);
        final GroupDto groupDtoUpdated = modelMapper.map(groupUpdated, GroupDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete (@PathVariable Long groupId){

        groupService.delete(groupId);

        return ResponseEntity.noContent().build();
    }
}
