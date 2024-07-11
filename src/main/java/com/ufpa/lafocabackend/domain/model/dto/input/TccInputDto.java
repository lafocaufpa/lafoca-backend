package com.ufpa.lafocabackend.domain.model.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufpa.lafocabackend.core.validation.ValidDate;
import com.ufpa.lafocabackend.domain.model.dto.output.LineOfResearchDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TccInputDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String url;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String slug;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @ValidDate
    private LocalDate date;

    private String abstractText;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<@NotNull String> lineOfResearchIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<LineOfResearchDto> linesOfResearch;

}
