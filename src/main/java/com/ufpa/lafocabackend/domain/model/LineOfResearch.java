package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LineOfResearch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String lineOfResearchId;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(nullable = false, length = 225)
    private String description;
}
