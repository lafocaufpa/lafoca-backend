package com.ufpa.lafocabackend.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer SkillId;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private List<Member> members;

}
