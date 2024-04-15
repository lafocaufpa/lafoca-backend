package com.ufpa.lafocabackend.api.controller;

import com.ufpa.lafocabackend.core.security.CheckSecurityPermissionMethods;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.UserPhoto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.domain.model.dto.MemberDto;
import com.ufpa.lafocabackend.domain.model.dto.input.MemberInputDto;
import com.ufpa.lafocabackend.domain.model.dto.output.MemberSummaryDto;
import com.ufpa.lafocabackend.domain.service.MemberService;
import com.ufpa.lafocabackend.domain.service.UserPhotoService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final UserPhotoService userPhotoService;

    public MemberController(MemberService memberService, ModelMapper modelMapper, UserPhotoService userPhotoService) {
        this.memberService = memberService;
        this.modelMapper = modelMapper;
        this.userPhotoService = userPhotoService;
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping
    public ResponseEntity<MemberDto> add (@RequestBody MemberInputDto memberInputDto) {

        final MemberDto memberSaved = modelMapper.map(memberService.save(memberInputDto), MemberDto.class);

        return ResponseEntity.ok(memberSaved);
    }

    @GetMapping("/id/{memberId}")
    public ResponseEntity<MemberDto> read (@PathVariable String memberId){

        final Member member = memberService.read(memberId);
        final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/search/{slug}")
    public ResponseEntity<MemberDto> getByName (@PathVariable String slug){

        final Member member = memberService.getMemberByName(slug);
        final MemberDto memberDto = modelMapper.map(member, MemberDto.class);
        return ResponseEntity.ok(memberDto);
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping("/generate-slug-all")
    public ResponseEntity<Void> generateSlugAll () {
            memberService.generateSlugAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<MemberDto>> list (@PageableDefault(size = 7) Pageable pageable){

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

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> update (@PathVariable String memberId, @RequestBody MemberInputDto memberInputDto){

        final Member memberUpdated = memberService.update(memberId, memberInputDto);
        final MemberDto memberDtoUpdated = modelMapper.map(memberUpdated, MemberDto.class);
        return ResponseEntity.ok(memberDtoUpdated);
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> delete (@PathVariable String memberId){

        memberService.delete(memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @PostMapping(value = "/{memberId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoDto> addPhoto(MultipartFile photo, @PathVariable String memberId) throws IOException {

        final Member member = memberService.read(memberId);

        String originalFilename = member.getMemberId()
                + "_"
                + member.getName()
                + Objects.requireNonNull
                        (photo.getOriginalFilename())
                .substring(photo.getOriginalFilename().lastIndexOf("."));

        final UserPhoto userPhoto = new UserPhoto();
        userPhoto.setUserPhotoId(String.valueOf(member.getMemberId()));
        userPhoto.setFileName(originalFilename);
        userPhoto.setSize(photo.getSize());
        userPhoto.setContentType(photo.getContentType());

        final UserPhoto photoSaved = userPhotoService.save(userPhoto, photo.getInputStream());

        member.setPhoto(photoSaved);
        memberService.save(member);

        final PhotoDto photoDto = modelMapper.map(photoSaved, PhotoDto.class);

        return ResponseEntity.ok(photoDto);
    }

    @GetMapping(value = "/{memberId}/photo")
    public ResponseEntity<?> getPhoto(@PathVariable String memberId) {

        final Member member = memberService.read(memberId);
        final UserPhoto photo = member.getPhoto();

        if (photo == null) {
            return ResponseEntity.notFound().build();
        }

        if (photo.getUrl() != null) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, photo.getUrl()).build();
        } else {

            final PhotoStorageService.RecoveredPhoto recoveredPhoto = userPhotoService.get(photo.getFileName());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, photo.getContentType());

            return ResponseEntity.ok().headers(headers).body(new InputStreamResource(recoveredPhoto.getInputStream()));
        }

    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping(value = "/{memberId}/photo")
    public ResponseEntity<Void> deletePhoto(@PathVariable String memberId) {

        memberService.read(memberId);
        userPhotoService.delete(String.valueOf(memberId));
        memberService.deletePhoto(memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{memberId}/functions-member/{functionMemberId}")
    public ResponseEntity<Void> associateFunction (@PathVariable String memberId, @PathVariable Long functionMemberId) {

        memberService.associateFunction(functionMemberId, memberId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @PutMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> associateSkill (@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.associateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }

    @CheckSecurityPermissionMethods.L1
    @DeleteMapping("/{memberId}/skills/{skillId}")
    public ResponseEntity<Void> disassociateSkill (@PathVariable String memberId, @PathVariable Long skillId) {

        memberService.disassociateSkill(memberId, skillId);

        return ResponseEntity.noContent().build();
    }
}
