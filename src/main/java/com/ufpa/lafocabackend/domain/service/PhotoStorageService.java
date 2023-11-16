package com.ufpa.lafocabackend.domain.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;

public interface PhotoStorageService {

    void armazenar(newPhoto newPhoto);

    RecoveredPhoto recuperar (String fileName);

    void deletar(String fileName);

    @Getter
    @Setter
    @Builder
    class newPhoto {

        private Long id;
        private String fileName;
        private String contentType;
        private InputStream inputStream;
    }

    @Getter
    @Builder
    class RecoveredPhoto {
        private InputStream inputStream;
        private String url;
        private String contentType;

        public boolean hasUrl () {
            return url != null;
        }

        public boolean hasInputStream () {
            return inputStream != null;
        }
    }
}
