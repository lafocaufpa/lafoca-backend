package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long projectId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String tittle;
    
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String year;

}
