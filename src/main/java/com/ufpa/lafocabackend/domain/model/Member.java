package com.ufpa.lafocabackend.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String memberId;

    @Column(nullable = false)
    private String name;

    private String slug;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 500)
    private String biography;

    @Column()
    private String linkPortifolio;

    @ManyToMany
    @JoinTable(name = "member_skill",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private Set<Skill> skills = new HashSet<>();

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private UserPhoto photo;

    @OneToOne
    @JoinColumn(name = "function_member_id")
    private FunctionMember functionMember;

    @OneToOne
    @JoinColumn(name = "tcc_id")
    private Tcc tcc;

    @ManyToMany
    @JoinTable(name = "member_article",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id"))
    private Set<Article> articles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "member_project",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    private Set<Project> projects = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void generateSlug() {

        this.slug = createSlug(this.name);

    }

    private String createSlug(String name) {
        String slug = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-{2,}", "-");
        String suffix = memberId.substring(0, memberId.indexOf("-"));

        return slug + "-" + suffix;

    }

    public boolean addSkill(Skill skill) {
        return skills.add(skill);
    }

    public boolean removeSkill(Skill skill) {
        return skills.remove(skill);
    }

    public boolean addProject(Project project) {
        return projects.add(project);
    }

    public boolean addArticles(Article article) {

        return articles.add(article);
    }

}
