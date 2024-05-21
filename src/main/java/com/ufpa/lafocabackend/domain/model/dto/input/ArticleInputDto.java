package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String journal;

    @NotBlank
    private String url;

    @NotNull
    private List<@NotNull String> lineOfResearchIds;
}