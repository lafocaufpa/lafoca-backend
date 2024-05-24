package com.ufpa.lafocabackend.domain.model.dto.input;

import com.ufpa.lafocabackend.domain.model.dto.output.LineOfResearchDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewsInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private List<@NotNull String> lineOfResearchIds;

}
