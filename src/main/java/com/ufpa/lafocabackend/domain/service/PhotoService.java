package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;
import com.ufpa.lafocabackend.domain.model.MemberPhoto;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.PhotoStorageService.RecoveredPhoto;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.repository.MemberUserPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

/**
 * Essa classe serve como Service para alterações de fotos de usuários (@{@link User} ou Member).
 * As entidades (Fotos) persistidas vinculadas tanto a User quanto a Member ficarão na tabela @{@link MemberPhoto}
 */
@Service
public class PhotoService {


    private final PhotoStorageService photoStorageService;
    private final MemberUserPhotoRepository photoRepository;

    public PhotoService(PhotoStorageService photoStorageService, MemberUserPhotoRepository memberUserPhotoRepository) {
        this.photoRepository = memberUserPhotoRepository;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public MemberPhoto save(MemberPhoto photo, InputStream inputStream) throws IOException {

        StoragePhotoUtils newPhoto = StoragePhotoUtils.builder()
                .fileName(photo.getFileName())
                .contentType(photo.getContentType())
                .contentLength(photo.getSize())
                .type(TypeEntityPhoto.User)
                .inputStream(inputStream)
                .build();

        final MemberPhoto photoSaved = photoRepository.save(photo);

        final String url = photoStorageService.armazenar(newPhoto);

        photoSaved.setUrl(url);

        return photoSaved;
    }

    public RecoveredPhoto get(String fileName) {

        return photoStorageService.recuperar(fileName);
    }

    @Transactional
    public void delete(String photoId) {

        final String fileName = photoRepository.findFileNameByPhotoId(photoId);
        photoRepository.removeMemberPhotoReference(photoId);
        photoRepository.deletePhotoByMemberId(photoId);

        final StoragePhotoUtils storagePhotoUtils = StoragePhotoUtils.builder().fileName(fileName).type(TypeEntityPhoto.User).build();

        photoStorageService.deletar(storagePhotoUtils);

        photoRepository.deleteById(photoId);
    }

}
