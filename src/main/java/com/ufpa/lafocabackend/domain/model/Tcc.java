package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tcc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long tccId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 25)
    private String date;

    @Column(nullable = false, length = 2083, unique = true)
    private String url;

}
