package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.file.CustomMultipartFile;
import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.Skill;
import com.ufpa.lafocabackend.domain.model.SkillPicture;
import com.ufpa.lafocabackend.domain.model.dto.output.PhotoDto;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.repository.SkillPictureRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createPhotoFilename;

@Service
public class SkillPictureService {

    private final PhotoStorageService photoStorageService;
    private final ModelMapper modelMapper;
    private final SkillPictureRepository skillPictureRepository;

    public SkillPictureService(PhotoStorageService photoStorageService, ModelMapper modelMapper, SkillPictureRepository skillPictureRepository) {
        this.photoStorageService = photoStorageService;
        this.modelMapper = modelMapper;
        this.skillPictureRepository = skillPictureRepository;
    }

    @Transactional
    public void delete(Skill skill) {

        Long skillId = skill.getSkillId();

        String photoFilename = skillPictureRepository.findSkillPictureFileNameBySkillPictureId(skillId);
        skillPictureRepository.removeSkillPictureReference(skillId);
        skillPictureRepository.deleteSkillPictureBySkillId(skillId);

        var storagePhotoUtils = StoragePhotoUtils
                .builder()
                .fileName(photoFilename)
                .type(TypeEntityPhoto.Skill).build();

        photoStorageService.deletar(storagePhotoUtils);
    }

    @Transactional
    public PhotoDto save(Skill skill, CustomMultipartFile photo) throws IOException {

        String formatedOffsetDateTime = LafocaUtils.getFormatedOffsetDateTime();

        String idPhoto = formatedOffsetDateTime + skill.getSkillId();

        String originalPhotoFilename = createPhotoFilename(idPhoto, photo.getOriginalFilename());

        String oldPhotoName = null;
        if(skill.getSkillPicture() != null){
            oldPhotoName = getFileNamePhoto(skill);
        }

        SkillPicture skillPicture = new SkillPicture();
        skillPicture.setSkillPictureId(skill.getSkillId());
        skillPicture.setFileName(originalPhotoFilename);
        skillPicture.setSize(photo.getSize());
        skillPicture.setDataUpdate(formatedOffsetDateTime);
        skillPicture.setContentType(photo.getContentType());

        final SkillPicture skillPictureSaved = skillPictureRepository.save(skillPicture);

        StoragePhotoUtils newPic = StoragePhotoUtils.builder()
                .id(String.valueOf(skill.getSkillId()))
                .fileName(skillPicture.getFileName())
                .contentType(skillPicture.getContentType())
                .contentLength(skillPicture.getSize())
                .type(TypeEntityPhoto.Skill)
                .inputStream(photo.getInputStream())
                .build();

        photoStorageService.deletar(StoragePhotoUtils.builder().fileName(oldPhotoName).type(TypeEntityPhoto.Skill).build());
        final String url = photoStorageService.armazenar(newPic);
        skillPictureSaved.setUrl(url);

        skill.setSkillPicture(skillPictureSaved);
        return modelMapper.map(skillPictureSaved, PhotoDto.class);
    }

    public String getFileNamePhoto(Skill skill) {
        return skill.getSkillPicture().getFileName();
    }

}
