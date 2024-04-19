package com.ufpa.lafocabackend.infrastructure.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ufpa.lafocabackend.core.storage.StorageProperties;
import com.ufpa.lafocabackend.core.utils.StoragePhotoUtils;
import com.ufpa.lafocabackend.core.utils.TypeEntityPhoto;

import java.io.InputStream;

public class S3FotoStorageService implements PhotoStorageService {

    private final StorageProperties storageProperties;

    private final AmazonS3 amazonS3;

    public S3FotoStorageService(StorageProperties storageProperties, AmazonS3 amazonS3) {
        this.storageProperties = storageProperties;
        this.amazonS3 = amazonS3;
    }

    @Override
    public String armazenar(StoragePhotoUtils newPhoto){

        String diretorio = getDiretorio(newPhoto.getType());

        String caminho = getCaminhoArquivo(diretorio, newPhoto.getFileName());

        final String bucket = storageProperties.getS3().getBucket();
        final PutObjectRequest putObjectRequest = getPutObjectRequest(newPhoto, bucket, caminho);
        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(bucket, caminho).toString();
    }

    private static PutObjectRequest getPutObjectRequest(StoragePhotoUtils newPhoto, String bucket, String diretorio) {
        final InputStream inputStream = newPhoto.getInputStream();
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(newPhoto.getContentType());
        objectMetadata.setContentLength(newPhoto.getContentLength());
        final CannedAccessControlList publicRead = CannedAccessControlList.PublicRead;

        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, diretorio, inputStream, objectMetadata);
        putObjectRequest.withCannedAcl(publicRead);
        return putObjectRequest;
    }

    @Override
    public RecoveredPhoto recuperar(String fileName) {

        return null;
    }

    @Override
    public void deletar(StoragePhotoUtils storagePhotoUtils) {

        final String bucket = storageProperties.getS3().getBucket();

        final String diretorioS3 = getDiretorio(storagePhotoUtils.getType());

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, getCaminhoArquivo(diretorioS3, storagePhotoUtils.getFileName()));
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir arquivo na Amazon S3.", e);
        }
    }

    private String getCaminhoArquivo(String diretorioS3, String fileName) {
        return String.format("%s/%s", diretorioS3, fileName);
    }

    private String getDiretorio (TypeEntityPhoto typeEntity){

        if(typeEntity == TypeEntityPhoto.User) {
            return storageProperties.getS3().getDiretorio_users();
        } else if (typeEntity == TypeEntityPhoto.News) {
            return storageProperties.getS3().getDiretorio_news();
        } else {
            return null;
        }
    }

}
