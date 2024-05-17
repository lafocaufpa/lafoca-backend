package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineOfResearchDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
