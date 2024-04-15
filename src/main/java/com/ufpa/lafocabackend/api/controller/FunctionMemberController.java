package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.FunctionMember;
import com.ufpa.lafocabackend.domain.model.dto.FunctionMemberDto;
import com.ufpa.lafocabackend.domain.service.FunctionMemberService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/functions-member")
public class FunctionMemberController {

    private final FunctionMemberService functionMemberService;
    private final ModelMapper modelMapper;

    public FunctionMemberController(FunctionMemberService functionMemberService, ModelMapper modelMapper) {
        this.functionMemberService = functionMemberService;
        this.modelMapper = modelMapper;
    }


    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<FunctionMemberDto> add (@RequestBody FunctionMemberDto functionMemberDto) {

        final FunctionMember functionMember = modelMapper.map(functionMemberDto, FunctionMember.class);

        final FunctionMemberDto functionMemberSaved = modelMapper.map(functionMemberService.save(functionMember), FunctionMemberDto.class);

        return ResponseEntity.ok(functionMemberSaved);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping("/{functionMemberId}")
    public ResponseEntity<FunctionMemberDto> read (@PathVariable Long functionMemberId){

        final FunctionMember functionMember = functionMemberService.read(functionMemberId);
        final FunctionMemberDto functionMemberDto = modelMapper.map(functionMember, FunctionMemberDto.class);
        return ResponseEntity.ok(functionMemberDto);
    }

    @CheckSecurityPermissionMethods.L1
    @GetMapping
    public ResponseEntity<Collection<FunctionMemberDto>> list (){

        final List<FunctionMember> list = functionMemberService.list();

        Type listType = new TypeToken<List<FunctionMemberDto>>() {

        }.getType();

        final List<FunctionMemberDto> map = modelMapper.map(list, listType);

        return ResponseEntity.ok(map);
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{functionMemberId}")
    public ResponseEntity<FunctionMemberDto> update (@PathVariable Long functionMemberId, @RequestBody FunctionMemberDto functionMemberDto){

        final FunctionMember functionMember = functionMemberService.read(functionMemberId);

        modelMapper.map(functionMemberDto, functionMember);

        final FunctionMember functionMemberUpdated = functionMemberService.update(functionMember);
        final FunctionMemberDto groupDtoUpdated = modelMapper.map(functionMemberUpdated, FunctionMemberDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{functionMemberId}")
    public ResponseEntity<Void> delete (@PathVariable Long functionMemberId){

        functionMemberService.delete(functionMemberId);

        return ResponseEntity.noContent().build();
    }
}