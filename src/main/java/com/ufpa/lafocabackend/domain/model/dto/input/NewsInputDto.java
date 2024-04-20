package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsInputDto {

    private String title;

    private String content;

    private String tags;

}
