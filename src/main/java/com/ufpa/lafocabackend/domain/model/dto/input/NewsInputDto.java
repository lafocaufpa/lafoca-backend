package com.ufpa.lafocabackend.domain.model.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsInputDto {

    private String tittle;

    private String content;

    private String tags;

    private Long userId;

}
