package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.MemberPhoto;
import com.ufpa.lafocabackend.domain.model.dto.PhotoDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.MemberPhotoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class MemberPhotoService {

    private final PhotoStorageService photoStorageService;
    private final MemberPhotoRepository memberPhotoRepository;
    private final ModelMapper modelMapper;
    private final MemberService memberService;

    public MemberPhotoService(PhotoStorageService photoStorageService, MemberPhotoRepository memberPhotoRepository, ModelMapper modelMapper, MemberService memberService) {
        this.memberPhotoRepository = memberPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.modelMapper = modelMapper;
        this.memberService = memberService;
    }

    @Transactional
    public void delete(String memberId) {

        String photoFilename = memberPhotoRepository.findMemberPhotoFileNameByPhotoId(memberId);
        memberPhotoRepository.removeMemberPhotoReference(memberId);
        memberPhotoRepository.deleteMemberPhotoByMemberId(memberId);

        var storagePhotoUtils = StoragePhotoUtils
                .builder()
                .fileName(photoFilename)
                .type(TypeEntityPhoto.Member).build();

        photoStorageService.deletar(storagePhotoUtils);

    }

    @Transactional
    public PhotoDto save(String memberId, MultipartFile photo) throws IOException {

        final Member member = memberService.read(memberId);

        String originalPhotoFilename = createPhotoFilename(member.getSlug(), photo.getOriginalFilename());

        MemberPhoto memberPhoto = new MemberPhoto();
        memberPhoto.setPhotoId(member.getMemberId());
        memberPhoto.setFileName(originalPhotoFilename);
        memberPhoto.setSize(photo.getSize());
        memberPhoto.setContentType(photo.getContentType());

        final MemberPhoto memberPhotoSaved = memberPhotoRepository.save(memberPhoto);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(memberPhoto.getFileName())
                .contentType(memberPhoto.getContentType())
                .contentLength(memberPhoto.getSize())
                .type(TypeEntityPhoto.Member)
                .inputStream(photo.getInputStream())
                .build();

        final String url = photoStorageService.armazenar(newPhoto);
        memberPhotoSaved.setUrl(url);

        member.setMemberPhoto(memberPhotoSaved);
        return modelMapper.map(memberPhotoSaved, PhotoDto.class);
    }
}
