package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.file.StandardCustomMultipartFile;
import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberResumed;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.domain.service.MemberPhotoService;
import com.ufpa.lafocabackend.domain.service.MemberService;
import jakarta.servlet.http.Part;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @CheckSecurityPermissionMethods.AdminOrEditor
    @PostMapping
        public ResponseEntity<MemberDto> add(@RequestBody @Valid MemberInputDto memberInputDto) {
    
            final MemberDto memberSaved = modelMapper.map(memberService.save(memberInputDto), MemberDto.class);
    
            return ResponseEntity.ok(memberSaved);
        }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> read(@PathVariable String memberId) {

        final Member member = memberService.read(memberId);
        final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
        return LafocaCacheUtil.createCachedResponseMember(memberDto);
    }

        @GetMapping("/read/{memberSlug}")
        public ResponseEntity<MemberDto> readBySlug(@PathVariable String memberSlug) {

            final Member member = memberService.readBySlug(memberSlug);
            final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
            return LafocaCacheUtil.createCachedResponseMember(memberDto);
        }

    @GetMapping
    @Deprecated
    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    public ResponseEntity<Page<MemberDto>> list(@PageableDefault(size = 10) Pageable pageable) {

        final Page<Member> list = memberService.list(pageable);

        Type listType = new TypeToken<List<MemberDto>>() {

        }.getType();

        final List<MemberDto> map = modelMapper.map(list.getContent(), listType);

        Page<MemberDto> memberDtoPage = new PageImpl<>(map, pageable, list.getTotalElements());

        return LafocaCacheUtil.createCachedResponseMember(memberDtoPage);
    }

    @GetMapping("/summarized")
    public ResponseEntity<Page<MemberSummaryDto>> listMembersSummarized(@PageableDefault(size = 7) Pageable pageable) {
        Page<MemberSummaryDto> memberSummaryDtos = memberService.listSummaryMember(pageable);
        Page<MemberSummaryDto> memberSummaryDtoPage = new PageImpl<>(memberSummaryDtos.getContent(), pageable, memberSummaryDtos.getTotalElements());
        return LafocaCacheUtil.createCachedResponseMember(memberSummaryDtoPage);

    }

    @GetMapping("/resumed")
    public ResponseEntity<Page<MemberResumed>> listResumedMembers(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "yearClassId", required = false) Long yearClassId,
            @RequestParam(value = "functionId", required = false) Long functionId,
            @RequestParam(value = "skillId", required = false) Long skillId,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<MemberResumed> memberResumeds;

        if ((fullName != null && !fullName.isEmpty()) || yearClassId != null || functionId != null || skillId != null) {
            memberResumeds = memberService.searchResumedMembersByFullNameYearClassIdFunctionIdAndSkillId(fullName, yearClassId, functionId, skillId, pageable);
        } else {
            memberResumeds = memberService.listResumedMembers(pageable);
        }

        Page<MemberResumed> memberResumedPage = new PageImpl<>(memberResumeds.getContent(), pageable, memberResumeds.getTotalElements());
        return LafocaCacheUtil.createCachedResponseMember(memberResumedPage);
    }


    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> update(@PathVariable String memberId, @RequestBody MemberInputDto memberInputDto) {

        final Member memberUpdated = memberService.update(memberId, memberInputDto);
        final MemberDto memberDtoUpdated = modelMapper.map(memberUpdated, MemberDto.class);
        return ResponseEntity.ok(memberDtoUpdated);
    }

    @CheckSecurityPermissionMethods.AdminOrEditor
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete(@PathVariable String memberId) {

        memberService.delete(memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PostMapping(value = "/{memberId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<PhotoDto> addPhoto(Part file, @PathVariable String memberId) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);

        Member member = memberService.read(memberId);

        return ResponseEntity.ok(memberPhotoService.save(member, customFile));
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PostMapping(value = "/read/{memberSlug}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhotoBySlug(Part file, @PathVariable String memberSlug) throws IOException {

        var customFile = new StandardCustomMultipartFile(file);

        Member member = memberService.readBySlug(memberSlug);
        return ResponseEntity.ok(memberPhotoService.save(member, customFile));
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping(value = "/{memberId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String memberId) {

        Member member = memberService.read(memberId);

        if(member.getMemberPhoto() != null){
            memberPhotoService.delete(member);
        }

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping(value = "/read/{memberSlug}/photo")
    public ResponseEntity<Void> deletePhotoBySlug(@PathVariable String memberSlug) {

        Member member = memberService.readBySlug(memberSlug);
        memberPhotoService.delete(member);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{memberId}/functions-member/{functionMemberId}")
    public ResponseEntity<Void> associateFunction(@PathVariable String memberId, @PathVariable Long functionMemberId) {

        memberService.associateFunction(functionMemberId, memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @PutMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> associateSkill(@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.associateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.AdminOrEditorOrModerator
    @DeleteMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> disassociateSkill(@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.disassociateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }
}
