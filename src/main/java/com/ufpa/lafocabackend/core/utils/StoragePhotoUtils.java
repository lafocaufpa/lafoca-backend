package com.ufpa.lafocabackend.core.utils;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class StoragePhotoUtils {

    private String id;
    private TypeEntityPhoto type;
    private String fileName;
    private String contentType;
    private long contentLength;
    private InputStream inputStream;

}


