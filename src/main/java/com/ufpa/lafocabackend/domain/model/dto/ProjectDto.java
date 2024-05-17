package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String type;

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

    @NotNull
    private String urlPhoto;
}
