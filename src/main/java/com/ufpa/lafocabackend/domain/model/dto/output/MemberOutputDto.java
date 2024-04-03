package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberOutputDto {
    private String name;
    private String slug;
    private String url;
    private String function;
}
