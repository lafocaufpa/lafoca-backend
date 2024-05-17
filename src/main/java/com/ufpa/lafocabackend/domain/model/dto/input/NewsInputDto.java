package com.ufpa.lafocabackend.domain.model.dto.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsInputDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String tags;

}
