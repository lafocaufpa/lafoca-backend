package com.ufpa.lafocabackend.infrastructure.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ufpa.lafocabackend.core.storage.StorageProperties;
import com.ufpa.lafocabackend.domain.model.News;
import com.ufpa.lafocabackend.domain.model.UserPhoto;
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
    public String armazenar(newPhoto newPhoto) {

        String diretorio = null;

        if(newPhoto.getNewsOrUser() instanceof UserPhoto){
        diretorio = getCaminhoArquivo(storageProperties.getS3().getDiretorio_users(), newPhoto.getFileName());
        } else if (newPhoto.getNewsOrUser() instanceof News){
            diretorio = getCaminhoArquivo(storageProperties.getS3().getDiretorio_news(), newPhoto.getFileName());
        }

        final String bucket = storageProperties.getS3().getBucket();
        final InputStream inputStream = newPhoto.getInputStream();
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newPhoto.getContentType());
        final CannedAccessControlList publicRead = CannedAccessControlList.PublicRead;

        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, diretorio, inputStream, objectMetadata);
        putObjectRequest.withCannedAcl(publicRead);
        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(bucket, diretorio).toString();
    }

    @Override
    public RecoveredPhoto recuperar(String fileName) {



        final String recoveredPath = getCaminhoArquivo(null, fileName);
        final URL url = amazonS3.getUrl(storageProperties.getS3().getBucket(), recoveredPath);

        return RecoveredPhoto.builder().url(url.toString()).build();
    }

    @Override
    public void deletar(String fileName) {

        final String bucket = storageProperties.getS3().getBucket();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, getCaminhoArquivo(null, fileName));
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir arquivo na Amazon S3.", e);
        }
    }

    private String getCaminhoArquivo(String diretorioS3, String fileName) {
        return String.format("%s/%s", diretorioS3, fileName);
    }
}
