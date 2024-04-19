package com.ufpa.lafocabackend.infrastructure.service;

import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

public interface PhotoStorageService {

    String armazenar(StoragePhotoUtils newPhoto);

    RecoveredPhoto recuperar (String fileName);

    void deletar(StoragePhotoUtils storagePhotoUtils);

    @Getter
    @Builder
    class RecoveredPhoto {
        private InputStream inputStream;
        private String contentType;

    }
}
