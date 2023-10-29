package com.ufpa.lafocabackend.domain.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

@Getter
@Setter
public class PhotoDto {

    private Long photoId;

    private String fileName;

    private Long size;

    private String contentType;

}
