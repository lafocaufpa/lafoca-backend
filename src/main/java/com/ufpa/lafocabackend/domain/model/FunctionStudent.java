package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FunctionStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long functionStudentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

}