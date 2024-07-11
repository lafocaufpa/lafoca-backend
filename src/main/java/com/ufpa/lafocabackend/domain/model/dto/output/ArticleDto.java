package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ArticleDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String slug;

    @NotBlank
    private String journal;

    private String abstractText;

    private String date;

    @NotBlank
    private String url;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<LineOfResearchDto> linesOfResearch;
}
