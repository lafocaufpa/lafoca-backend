package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoDto {

    private String photoId;

    private String fileName;

    private Long size;

    private String contentType;

    private String url;

}
