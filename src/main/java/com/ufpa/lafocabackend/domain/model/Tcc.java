package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

import java.time.LocalDate;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

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

    @Column(nullable = false, length = 500, unique = true)
    private String slug;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(700)")
    private String url;

    @PreUpdate
    @PrePersist
    public void generateSlug() {

        this.slug = createSlug(this.name, date.toString());
    }

}
