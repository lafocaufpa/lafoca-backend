package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private String type;
    private String title;
    private String slug;
    private String description;
    private Boolean completed;
    private String year;
    private String urlPhoto;
}
