package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    private String projectId;
    private String type;
    private String title;
    private String slug;
    private String description;
    private Boolean completed;
    private String year;
    private String urlPhoto;
}
