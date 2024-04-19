package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class NewsOutput {

    private String slug;
    private String tittle;
    private OffsetDateTime newsDate;
    private String description;
    private String urlPhoto;
}
