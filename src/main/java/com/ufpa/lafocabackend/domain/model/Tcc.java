package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    @Column(columnDefinition = "DATE", nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 2083, unique = true)
    private String url;

}
