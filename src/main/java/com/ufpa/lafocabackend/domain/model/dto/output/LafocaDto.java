package com.ufpa.lafocabackend.domain.model.dto.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LafocaDto {

    private Long id;
    private int numberOfMembers;
    private int numberOfDefendedTCCs;
    private int numberOfPublishedArticles;
    private int numberOfProjects;
    private int yearOfCreation;
}
