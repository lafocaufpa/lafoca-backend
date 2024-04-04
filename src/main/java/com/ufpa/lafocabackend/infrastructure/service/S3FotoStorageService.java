package com.ufpa.lafocabackend.infrastructure.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ufpa.lafocabackend.core.storage.StorageProperties;

import java.io.InputStream;

public class S3FotoStorageService implements PhotoStorageService {

    private final StorageProperties storageProperties;

    private final AmazonS3 amazonS3;

    public S3FotoStorageService(StorageProperties storageProperties, AmazonS3 amazonS3) {
        this.storageProperties = storageProperties;
        this.amazonS3 = amazonS3;
    }

    @Override
    public String armazenar(StorageUtils newPhoto) {

        String diretorio = null;

        if(newPhoto.getType().equals(StorageUtils.FileType.TypeUser)){
        diretorio = getCaminhoArquivo(storageProperties.getS3().getDiretorio_users(), newPhoto.getFileName());
        } else if (newPhoto.getType().equals(StorageUtils.FileType.TypeNews)){
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

        return null;
    }

    @Override
    public void deletar(StorageUtils storageUtils) {

        final String bucket = storageProperties.getS3().getBucket();

        final String diretorioS3 = getDiretorio(storageUtils.getType());

        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, getCaminhoArquivo(diretorioS3, storageUtils.getFileName()));
        try {
            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível excluir arquivo na Amazon S3.", e);
        }
    }

    private String getCaminhoArquivo(String diretorioS3, String fileName) {
        return String.format("%s/%s", diretorioS3, fileName);
    }

    private String getDiretorio (StorageUtils.FileType typeEntity){

        return (typeEntity == StorageUtils.FileType.TypeUser)
                ? storageProperties.getS3().getDiretorio_users()
                : storageProperties.getS3().getDiretorio_news();
    }

}
