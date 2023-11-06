package com.ufpa.lafocabackend.infrastructure.service;

import com.ufpa.lafocabackend.core.storage.StorageProperties;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//@Service
public class LocalPhotoStorageService implements PhotoStorageService {

    private final StorageProperties storageProperties;
//    @Value("${lafoca.storage.local.path}")
//    private Path localPath;

    public LocalPhotoStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }


    @Override
    public void armazenar(newPhoto newPhoto) {

        Path path = getFilePath(newPhoto.getId(), newPhoto.getFileName());

        try {
            FileCopyUtils.copy(newPhoto.getInputStream(), Files.newOutputStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível armazenar o arquivo.");
        }
    }

    @Override
    public RecoveredPhoto recuperar(String fileName) {
        return null;
    }

    private Path getFilePath (Long id, String fileName){

        final Path resolve = storageProperties.getLocal().getDiretorioFotos().resolve(String.valueOf(id));
        if(!resolve.toFile().exists()){
            resolve.toFile().mkdirs();
        }

        return resolve.resolve(fileName);
    }
}
