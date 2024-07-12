package com.ufpa.lafocabackend.domain.model.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufpa.lafocabackend.domain.model.MemberInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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
    private String abstractText;

    private String endDate;

    private String modality;

    private Set<MemberInfo> members = new HashSet<>();

    @NotNull
    private String date;

    private String urlPhoto;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<LineOfResearchDto> linesOfResearch;
}
