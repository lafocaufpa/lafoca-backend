package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lafoca {

    @Id
    private Long id;
    private int numberOfMembers;
    private int numberOfDefendedTCCs;
    private int numberOfPublishedArticles;
    private int numberOfProjects;
    private int yearOfCreation;

    @CreationTimestamp
    private OffsetDateTime offsetDateTime;
}
