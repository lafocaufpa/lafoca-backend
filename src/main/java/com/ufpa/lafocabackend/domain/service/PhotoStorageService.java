package com.ufpa.lafocabackend.domain.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;

public interface PhotoStorageService {

    void armazenar(newPhoto newPhoto);

    @Getter
    @Setter
    @Builder
    class newPhoto {

        private Long id;
        private String fileName;
        private InputStream inputStream;
    }
}
