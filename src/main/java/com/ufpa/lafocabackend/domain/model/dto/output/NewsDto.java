package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
public class NewsDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String slug;

    private String title;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime newsDate;

    private String description;

    private Set<LineOfResearchDto> linesOfResearch;

    private String content;

    private String urlPhoto;
}
