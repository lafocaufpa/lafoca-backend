package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectSummaryDto {

    private String projectId;
    private String type;
    private String tittle;
    private String description;
    private Boolean completed;
    private String year;
    private String urlPhoto;
}
