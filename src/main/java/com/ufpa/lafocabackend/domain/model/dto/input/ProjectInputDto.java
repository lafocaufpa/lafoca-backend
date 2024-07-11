package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String abstractText;

    @NotNull
    private Boolean completed;

    @NotNull
    private String date;
    
    private String urlPhoto;

    @NotNull
    private List<@NotNull String> lineOfResearchIds;
}