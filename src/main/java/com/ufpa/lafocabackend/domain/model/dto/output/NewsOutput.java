package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
public class NewsOutput {

    private String id;
    private String slug;
    private String title;
    private OffsetDateTime newsDate;
    private String description;
    private Set<LineOfResearchDto> linesOfResearch;
    private String urlPhoto;
}
