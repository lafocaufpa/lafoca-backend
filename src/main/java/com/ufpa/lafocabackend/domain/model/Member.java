package com.ufpa.lafocabackend.domain.model;

import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {

    @Id
    @EqualsAndHashCode.Include
    private String memberId;

    @Column(nullable = false, length = 225)
    private String fullName;

    @Column(nullable = false, length = 225)
    private String displayName;

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false, length = 225)
    private String description;

    @Column(nullable = false, unique = true, length = 225)
    private String email;

    @Column(nullable = false, length = 1000)
    private String biography;

    @Column(nullable = true, unique = true, length = 225)
    private String linkPortifolio;

    @Column(nullable = true, unique = true, length = 225)
    private String linkLinkedin;

    @Column(columnDefinition = "integer")
    private Integer yearClass;

    @ManyToMany
    @JoinTable(
        name = "member_skill",
        joinColumns = @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(name = "fk_member_skill_member_id")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "skill_id",
            foreignKey = @ForeignKey(name = "fk_member_skill_skill_id")
        )
    )
    private Set<Skill> skills = new HashSet<>();

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "photo_id", foreignKey = @ForeignKey(name = "fk_member_photo_id"))
    private MemberPhoto memberPhoto;

    @ManyToOne
    @JoinColumn(name = "function_member_id", foreignKey = @ForeignKey(name = "fk_member_function_member_id"))
    private FunctionMember functionMember;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tcc_id", foreignKey = @ForeignKey(name = "fk_member_tcc_id"))
    private Tcc tcc;

    @ManyToMany
    @JoinTable(name = "member_article",
            joinColumns = @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_article_member_id")),
            inverseJoinColumns = @JoinColumn(name = "article_id", foreignKey = @ForeignKey(name = "fk_member_article_article_id")))
    private Set<Article> articles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "member_project",
            joinColumns = @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_member_project_member_id")),
            inverseJoinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_member_project_project_id")))
    private Set<Project> projects = new HashSet<>();

    @PrePersist
    private void prePersist() {
        this.memberId = UUID.randomUUID().toString();
        preUpdate();
    }

    @PreUpdate
    private void preUpdate() {
        this.slug = createSlug(this.fullName, this.memberId);
        if(this.linkLinkedin != null){
            this.linkLinkedin = normalizeUrl(this.linkLinkedin);
        }

        if(this.linkPortifolio != null){
            this.linkPortifolio = normalizeUrl(this.linkPortifolio);
        }
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

    private String normalizeUrl(String url) {
        return LafocaUtils.normalizeUrl(url);
    }


}
