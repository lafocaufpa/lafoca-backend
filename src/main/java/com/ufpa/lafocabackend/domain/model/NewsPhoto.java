package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NewsPhoto {

    @Id
    @EqualsAndHashCode.Include
    private Long newsPhotoId;
    private Long size;
    private String contentType;
    private String url;
    private String fileName;

}
