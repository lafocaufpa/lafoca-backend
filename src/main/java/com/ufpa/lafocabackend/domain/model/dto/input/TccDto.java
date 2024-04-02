package com.ufpa.lafocabackend.domain.model.dto.input;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TccDto {
    private Long tccId;
    private String name;
    private String url;
    private String date;

}
