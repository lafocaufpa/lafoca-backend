package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LafocaDto {

    private int totalMembers;
    private int totalProjects;
    private int totalTcc;
    private int totalArticles;
}
