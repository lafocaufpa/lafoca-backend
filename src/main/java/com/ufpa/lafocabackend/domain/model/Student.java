package com.ufpa.lafocabackend.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    @ManyToMany
    @JoinTable(name = "students_skills",
            joinColumns = @JoinColumn(name = "id_student"),
            inverseJoinColumns = @JoinColumn(name = "id_skill"))
    private List<Skill> skills = new ArrayList<>();

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private UserPhoto photo;

    @OneToOne
    @JoinColumn(name = "function_student_id", nullable = false)
    private FunctionStudent functionStudent;

    @OneToOne(mappedBy = "student")
    @JoinColumn(name = "tcc_id")
    private Tcc tcc;

    @ManyToMany
    @JoinTable(name = "student_articles",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id"))
    private List<Article> articles = new ArrayList<>();

    public boolean addSkill(Skill skill) {
        return skills.add(skill);
    }

    public boolean removeSkill(Skill skill) {
        return skills.remove(skill);
    }
}
