package com.ufpa.lafocabackend.domain.model.dto.input;

import com.ufpa.lafocabackend.domain.model.MemberInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProjectInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String abstractText;

    @NotNull
    private String date;

    private String endDate;

    private String modality;

    private Set<MemberInfo> members = new HashSet<>();

    private String urlPhoto;

    @NotNull
    private List<@NotNull String> lineOfResearchIds;
}