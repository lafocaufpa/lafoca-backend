package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class YearClass {

    @Id
    @EqualsAndHashCode.Include
    private Long yearClassId;

    @Column(nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "yearClass")
    private Set<Member> members = new HashSet<>();

    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (this.year != null) {
            this.yearClassId = Long.valueOf(this.year);
        }
    }

}