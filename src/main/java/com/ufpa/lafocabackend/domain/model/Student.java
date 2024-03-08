package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long studentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String biography;

    @Column(nullable = false)
    private String linkPortifolio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_skills",
            joinColumns = @JoinColumn(name = "id_students"),
            inverseJoinColumns = @JoinColumn(name = "id_skill"))
    private List<Skills> skills = new ArrayList<>();

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private UserPhoto photo;

    @OneToOne
    @JoinColumn(name = "functionStudentId")
    private FunctionStudent functionStudent;

    public boolean addSkill(Skills skill) {
        return skills.add(skill);
    }

    public boolean removeSkill(Skills skill) {
        return skills.remove(skill);
    }
}
