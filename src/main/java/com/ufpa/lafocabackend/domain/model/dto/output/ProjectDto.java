package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProjectDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    @NotBlank
    private String description;

    @NotNull
    private Boolean completed;

    @NotNull
    private String year;

    private String urlPhoto;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<LineOfResearchDto> linesOfResearch;
}
