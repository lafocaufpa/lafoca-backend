package com.ufpa.lafocabackend.core.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.ufpa.lafocabackend.domain.service.PhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.LocalPhotoStorageService;
import com.ufpa.lafocabackend.infrastructure.service.S3FotoStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ufpa.lafocabackend.core.storage.StorageProperties.*;

@Configuration
public class StorageConfig {

    private final StorageProperties storageProperties;

    public StorageConfig(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }


    @Bean
    public AmazonS3 amazonS3 () {

        final BasicAWSCredentials credentials = new BasicAWSCredentials
                (storageProperties.getS3().getIdChaveAcesso(),
                        storageProperties.getS3().getChaveAcessoSecreta());

        return AmazonS3ClientBuilder.standard().
        withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(storageProperties.getS3().getRegiao()).build();
    }

    @Bean
    public PhotoStorageService photoStorageService () {

        if(TipoStorage.S3.equals(storageProperties.getTipoStorage())){
            return new S3FotoStorageService(storageProperties, amazonS3());
        } else {
            return new LocalPhotoStorageService(storageProperties);
        }

    }
}
