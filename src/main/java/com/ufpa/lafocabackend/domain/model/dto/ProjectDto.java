package com.ufpa.lafocabackend.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    private Long projectId;
    private String type;
    private String tittle;
    private String description;
    private String status;
    private String year;
}
