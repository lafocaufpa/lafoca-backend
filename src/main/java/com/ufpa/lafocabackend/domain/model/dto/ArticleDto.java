package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String slug;
    private String journal;
    private String url;
}
