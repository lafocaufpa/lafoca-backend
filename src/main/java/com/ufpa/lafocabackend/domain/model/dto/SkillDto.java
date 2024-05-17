package com.ufpa.lafocabackend.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Setter
public class SkillDto {

    @JsonProperty(access = READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

}
