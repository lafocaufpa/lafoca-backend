package com.ufpa.lafocabackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FunctionMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long functionMemberId;

    @Column(nullable = false, length = 225)
    private String name;


    @Column(nullable = false, length = 225)
    private String description;

    public FunctionMember() {

    }
}