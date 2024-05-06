package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.file.MultipartFileWrapper;
import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.dto.MemberDto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.domain.service.MemberPhotoService;
import com.ufpa.lafocabackend.domain.service.MemberService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberPhotoService memberPhotoService;

    public MemberController(MemberService memberService, ModelMapper modelMapper, MemberPhotoService memberPhotoService) {
        this.memberService = memberService;
        this.modelMapper = modelMapper;
        this.memberPhotoService = memberPhotoService;
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping
    public ResponseEntity<MemberDto> add(@RequestBody MemberInputDto memberInputDto) {

        final MemberDto memberSaved = modelMapper.map(memberService.save(memberInputDto), MemberDto.class);

        return ResponseEntity.ok(memberSaved);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> read(@PathVariable String memberId) {

        final Member member = memberService.read(memberId);
        final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/search/{memberSlug}")
    public ResponseEntity<MemberDto> readBySlug(@PathVariable String memberSlug) {

        final Member member = memberService.readBySlug(memberSlug);
        final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping
    public ResponseEntity<Page<MemberDto>> list(@PageableDefault(size = 7) Pageable pageable) {

        final Page<Member> list = memberService.list(pageable);

        Type listType = new TypeToken<List<MemberDto>>() {

        }.getType();

        final List<MemberDto> map = modelMapper.map(list.getContent(), listType);

        Page<MemberDto> memberDtoPage = new PageImpl<>(map, pageable, list.getTotalElements());

        return ResponseEntity.ok(memberDtoPage);
    }

    @GetMapping("/summarized")
    public ResponseEntity<Page<MemberSummaryDto>> listMembersSummarized(@PageableDefault(size = 7) Pageable pageable) {
        Page<MemberSummaryDto> memberSummaryDtos = memberService.listSummaryMember(pageable);
        Page<MemberSummaryDto> memberSummaryDtoPage = new PageImpl<>(memberSummaryDtos.getContent(), pageable, memberSummaryDtos.getTotalElements());
        return ResponseEntity.ok(memberSummaryDtoPage);

    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> update(@PathVariable String memberId, @RequestBody MemberInputDto memberInputDto) {

        final Member memberUpdated = memberService.update(memberId, memberInputDto);
        final MemberDto memberDtoUpdated = modelMapper.map(memberUpdated, MemberDto.class);
        return ResponseEntity.ok(memberDtoUpdated);
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable String memberId) {

        memberService.delete(memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping(value = "/{memberId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(MultipartFile photo, @PathVariable String memberId) throws IOException {

        Member member = memberService.read(memberId);
        return ResponseEntity.ok(memberPhotoService.save(member, new MultipartFileWrapper(photo)));
    }

    @CheckSecurityPermissionMethods.Level1
    @PostMapping(value = "/search/{memberSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhotoBySlug(MultipartFile photo, @PathVariable String memberSlug) throws IOException {

        Member member = memberService.readBySlug(memberSlug);
        return ResponseEntity.ok(memberPhotoService.save(member, new MultipartFileWrapper(photo)));
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping(value = "/{memberId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String memberId) {

        Member member = memberService.read(memberId);
        memberPhotoService.delete(member);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping(value = "/search/{memberSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String memberSlug) {

        Member member = memberService.readBySlug(memberSlug);
        memberPhotoService.delete(member);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{memberId}/functions-member/{functionMemberId}")
    public ResponseEntity<Void> associateFunction(@PathVariable String memberId, @PathVariable Long functionMemberId) {

        memberService.associateFunction(functionMemberId, memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @PutMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> associateSkill(@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.associateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.Level1
    @DeleteMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> disassociateSkill(@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.disassociateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }
}
