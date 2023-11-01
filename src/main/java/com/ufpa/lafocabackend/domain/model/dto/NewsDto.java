package com.ufpa.lafocabackend.domain.model.dto;

import com.ufpa.lafocabackend.domain.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class NewsDto {

    private String slug;

    private String tittle;

    private OffsetDateTime newsDate;

    private String description;

    private String tags;

    private String content;

    private User user;
}
