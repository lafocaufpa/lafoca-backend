package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lafoca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lafocaId;

    private int numberOfMembers;

    private int numberOfDefendedTCCs;

    private int numberOfPublishedArticles;

    private int numberOfProjects;

    @CreationTimestamp
    private OffsetDateTime dateTime;

}
