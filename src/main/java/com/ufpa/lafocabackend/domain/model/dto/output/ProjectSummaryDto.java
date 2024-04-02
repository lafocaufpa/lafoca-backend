package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectSummaryDto {

    private Long projectId;
    private String type;
    private String tittle;
    private String description;
    private String status;
    private String year;
}
