package com.ufpa.lafocabackend.infrastructure.service;

import com.ufpa.lafocabackend.domain.service.PhotoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalPhotoStorageService implements PhotoStorageService {

    @Value("${lafoca.storage.local.path}")
    private Path localPath;
    @Override
    public void armazenar(newPhoto newPhoto) {

        Path path = getFilePath(newPhoto.getId(), newPhoto.getFileName());

        try {
            FileCopyUtils.copy(newPhoto.getInputStream(), Files.newOutputStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível armazenar o arquivo.");
        }
    }

    private Path getFilePath (Long id, String fileName){

        final Path resolve = localPath.resolve(String.valueOf(id));
        if(!resolve.toFile().exists()){
            resolve.toFile().mkdirs();
        }

        return resolve.resolve(fileName);
    }
}