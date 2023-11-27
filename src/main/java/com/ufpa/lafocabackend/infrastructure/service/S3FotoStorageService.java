package com.ufpa.lafocabackend.infrastructure.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ufpa.lafocabackend.core.storage.StorageProperties;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService;

import java.io.InputStream;
import java.net.URL;

public class S3FotoStorageService implements PhotoStorageService {

    private final StorageProperties storageProperties;

    private AmazonS3 amazonS3;

    public S3FotoStorageService(StorageProperties storageProperties, AmazonS3 amazonS3) {
        this.storageProperties = storageProperties;
        this.amazonS3 = amazonS3;
    }

    @Override
    public void armazenar(newPhoto newPhoto) {

        final String caminho = getCaminhoArquivo(newPhoto.getFileName());
        final String bucket = storageProperties.getS3().getBucket();
        final InputStream inputStream = newPhoto.getInputStream();
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newPhoto.getContentType());
        final CannedAccessControlList publicRead = CannedAccessControlList.PublicRead;

        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, caminho, inputStream, objectMetadata);
        putObjectRequest.withCannedAcl(publicRead);
        amazonS3.putObject(putObjectRequest);
    }

    @Override
    public RecoveredPhoto recuperar(String fileName) {

        final String recoveredPath = getCaminhoArquivo(fileName);
        final URL url = amazonS3.getUrl(storageProperties.getS3().getBucket(), recoveredPath);

        return RecoveredPhoto.builder().url(url.toString()).build();
    }

    @Override
    public void deletar(String fileName) {

        final String bucket = storageProperties.getS3().getBucket();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, getCaminhoArquivo(fileName));
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir arquivo na Amazon S3.", e);
        }
    }

    private String getCaminhoArquivo(String fileName) {
        return String.format("%s/%s", storageProperties.getS3().getDiretorio(), fileName);
    }
}
