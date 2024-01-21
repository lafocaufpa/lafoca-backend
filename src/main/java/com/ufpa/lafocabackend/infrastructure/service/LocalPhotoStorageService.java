package com.ufpa.lafocabackend.infrastructure.service;

import com.ufpa.lafocabackend.core.storage.StorageProperties;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class LocalPhotoStorageService implements PhotoStorageService {

    private final StorageProperties storageProperties;

    public LocalPhotoStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public String armazenar(StorageUtils newPhoto) {

        Path path = getFilePath(newPhoto.getFileName().split("_")[0], newPhoto.getFileName());

        try {
            FileCopyUtils.copy(newPhoto.getInputStream(), Files.newOutputStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível armazenar o arquivo.");
        }

        return null;
    }

    @Override
    public RecoveredPhoto recuperar(String fileName) {

        try {
            final Path diretorioFotos = storageProperties.getLocal().getDiretorioFotos();

            if (isPhotoUser(fileName)) {
                final String[] idBeforeUnderscore = fileName.split("_");
                String idPath = idBeforeUnderscore[0];

                Path path = diretorioFotos.resolve(idPath);
                path = path.resolve(fileName);

                InputStream inputStream = Files.newInputStream(path);

                return RecoveredPhoto.builder().inputStream(inputStream).build();
            } else {

                final String[] fileNameBeforeDot = fileName.split("\\.");

                final Path path = diretorioFotos.resolve(fileNameBeforeDot[0]);
                final Path resolve = path.resolve(fileName);

                final InputStream inputStream = Files.newInputStream(resolve);

                return RecoveredPhoto.builder().inputStream(inputStream).build();
            }


        } catch (IOException e) {
            throw new RuntimeException("Não foi possível recuperar a foto: " + e);
        }
    }

    @Override
    public void deletar(StorageUtils storageUtils) {
        try {
            final Path diretorioFotos = storageProperties.getLocal().getDiretorioFotos();
            final String[] idBeforeUnderscore = storageUtils.getFileName().split("_");
            String idPath = idBeforeUnderscore[0];

            Path path = diretorioFotos.resolve(idPath);

            deletarPasta(path);

//            path = path.resolve(fileName);

            //src/main/resources/upload/photos/2/2_joao.jpg
//            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível deletar a foto: " + e);
        }
    }

    private Path getFilePath(String id, String fileName) {

        final Path resolve = storageProperties.getLocal().getDiretorioFotos().resolve(String.valueOf(id));
        if (!resolve.toFile().exists()) {
            resolve.toFile().mkdirs();
        }

        return resolve.resolve(fileName);
    }

    public void deletarPasta(Path diretorio) throws IOException {
        try {
            Files.deleteIfExists(diretorio);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(diretorio, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            Files.deleteIfExists(diretorio);
        }
    }

    public boolean isPhotoUser(String fileName) {

        return fileName.contains("_");
    }
}
