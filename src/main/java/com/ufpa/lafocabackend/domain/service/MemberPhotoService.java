package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.Member;
import com.ufpa.lafocabackend.domain.model.MemberPhoto;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.MemberPhotoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class MemberPhotoService {

    private final PhotoStorageService photoStorageService;
    private final MemberPhotoRepository memberPhotoRepository;
    private final ModelMapper modelMapper;

    public MemberPhotoService(PhotoStorageService photoStorageService, MemberPhotoRepository memberPhotoRepository, ModelMapper modelMapper) {
        this.memberPhotoRepository = memberPhotoRepository;
        this.photoStorageService = photoStorageService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void delete(Member member) {

        String memberId = member.getMemberId();

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
    public PhotoDto save(Member member, CustomMultipartFile photo) throws IOException {

        String formatedOffsetDateTime = LafocaUtils.getFormatedOffsetDateTime();

        String idPhoto = formatedOffsetDateTime + member.getMemberId();

        String originalPhotoFilename = createPhotoFilename(idPhoto, photo.getOriginalFilename());

        String oldPhotoName = null;
        if(member.getMemberPhoto() != null){
            oldPhotoName = getFileNamePhoto(member);
        }

        MemberPhoto memberPhoto = new MemberPhoto();
        memberPhoto.setPhotoId(member.getMemberId());
        memberPhoto.setFileName(originalPhotoFilename);
        memberPhoto.setSize(photo.getSize());
        memberPhoto.setDataUpdate(formatedOffsetDateTime);
        memberPhoto.setContentType(photo.getContentType());

        final MemberPhoto memberPhotoSaved = memberPhotoRepository.save(memberPhoto);

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .id(member.getMemberId())
                .fileName(memberPhoto.getFileName())
                .contentType(memberPhoto.getContentType())
                .contentLength(memberPhoto.getSize())
                .type(TypeEntityPhoto.Member)
                .inputStream(photo.getInputStream())
                .build();

        photoStorageService.deletar(StoragePhotoUtils.builder().fileName(oldPhotoName).type(TypeEntityPhoto.Member).build());
        final String url = photoStorageService.armazenar(newPhoto);
        memberPhotoSaved.setUrl(url);

        member.setMemberPhoto(memberPhotoSaved);
        return modelMapper.map(memberPhotoSaved, PhotoDto.class);
    }

    public String getFileNamePhoto(Member member) {
        return member.getMemberPhoto().getFileName();
    }

}
