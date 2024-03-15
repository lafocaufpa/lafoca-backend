package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDto {

    private Long ArticleId;
    private String name;
    private String journal;
    private String url;
}
