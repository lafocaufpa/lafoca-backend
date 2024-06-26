package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.FunctionMember;
import com.ufpa.lafocabackend.domain.model.dto.output.FunctionMemberDto;
import com.ufpa.lafocabackend.domain.service.FunctionMemberService;
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
@RequestMapping("/functions-member")
public class FunctionMemberController {

    private final FunctionMemberService functionMemberService;
    private final ModelMapper modelMapper;

    public FunctionMemberController(FunctionMemberService functionMemberService, ModelMapper modelMapper) {
        this.functionMemberService = functionMemberService;
        this.modelMapper = modelMapper;
    }


    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<FunctionMemberDto> add (@RequestBody @Valid FunctionMemberDto functionMemberDto) {

        final FunctionMember functionMember = modelMapper.map(functionMemberDto, FunctionMember.class);

        final FunctionMemberDto functionMemberSaved = modelMapper.map(functionMemberService.save(functionMember), FunctionMemberDto.class);

        return ResponseEntity.ok(functionMemberSaved);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping("/{functionMemberId}")
    public ResponseEntity<FunctionMemberDto> read (@PathVariable Long functionMemberId){

        final FunctionMember functionMember = functionMemberService.read(functionMemberId);
        final FunctionMemberDto functionMemberDto = modelMapper.map(functionMember, FunctionMemberDto.class);
        return ResponseEntity.ok(functionMemberDto);
    }

    @CheckSecurityPermissionMethods.Level1
    @GetMapping
    public ResponseEntity<Page<FunctionMemberDto>> list(@RequestParam(value = "name", required = false) String name, Pageable pageable) {
        Page<FunctionMember> list;

        if (name != null && !name.isEmpty()) {
            list = functionMemberService.searchByName(name, pageable);
        } else {
            list = functionMemberService.list(pageable);
        }

        Type listType = new TypeToken<List<FunctionMemberDto>>() {}.getType();
        List<FunctionMemberDto> map = modelMapper.map(list.getContent(), listType);
        PageImpl<FunctionMemberDto> functionMemberDtos = new PageImpl<>(map, pageable, list.getTotalElements());

        return LafocaCacheUtil.createCachedResponseFunctionMember(functionMemberDtos);
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{functionMemberId}")
    public ResponseEntity<FunctionMemberDto> update (@PathVariable Long functionMemberId, @RequestBody FunctionMemberDto functionMemberDto){

        final FunctionMember functionMember = functionMemberService.read(functionMemberId);

        modelMapper.map(functionMemberDto, functionMember);

        final FunctionMember functionMemberUpdated = functionMemberService.update(functionMember);
        final FunctionMemberDto groupDtoUpdated = modelMapper.map(functionMemberUpdated, FunctionMemberDto.class);
        return ResponseEntity.ok(groupDtoUpdated);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{functionMemberId}")
    public ResponseEntity<Void> delete (@PathVariable Long functionMemberId){

        functionMemberService.delete(functionMemberId);

        return ResponseEntity.noContent().build();
    }
}
