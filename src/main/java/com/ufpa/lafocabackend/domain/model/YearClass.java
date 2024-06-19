package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "year_class")
public class YearClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long yearClassId;

    @Column(nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "yearClass")
    private Set<Member> members = new HashSet<>();

}