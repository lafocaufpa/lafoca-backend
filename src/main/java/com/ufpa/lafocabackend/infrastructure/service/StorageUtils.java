package com.ufpa.lafocabackend.infrastructure.service;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class StorageUtils {

    private FileType type;
    private String fileName;
    private String contentType;
    private InputStream inputStream;
    public enum FileType {
        TypeNews,
        TypeUser
    }
}


