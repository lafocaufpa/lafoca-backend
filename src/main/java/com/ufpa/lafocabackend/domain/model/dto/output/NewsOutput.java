package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class NewsOutput {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String slug;
    private String title;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime newsDate;
    private String description;
    private String urlPhoto;
}
