package com.ufpa.lafocabackend.domain.service;

import com.ufpa.lafocabackend.infrastructure.service.StorageUtils;
import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

public interface PhotoStorageService {

    String armazenar(StorageUtils newPhoto);

    RecoveredPhoto recuperar (String fileName);

    void deletar(StorageUtils storageUtils);

    @Getter
    @Builder
    class RecoveredPhoto {
        private InputStream inputStream;
        private String contentType;

    }
}
