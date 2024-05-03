package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class LafocaDto {

    private Long lafocaId;
    private int numberOfMembers;
    private int numberOfDefendedTCCs;
    private int numberOfPublishedArticles;
    private int numberOfProjects;
    private OffsetDateTime dateTime;

}
