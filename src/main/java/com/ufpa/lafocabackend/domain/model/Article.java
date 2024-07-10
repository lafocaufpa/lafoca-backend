package com.ufpa.lafocabackend.domain.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@RequiredArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long ArticleId;

    @Column(nullable = false, length = 225)
    private String title;

    @Column(nullable = false, length = 500, unique = true)
    private String slug;

    @Column(nullable = false, length = 225)
    private String journal;

    @Column(length = 4)
    private String year;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(700)")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String articleAbstract;

    @ManyToMany
    @JoinTable(name = "article_line_of_research",
            joinColumns = @JoinColumn(name = "article_id", foreignKey = @ForeignKey(name = "fk_article_research_id")),
            inverseJoinColumns = @JoinColumn(name = "line_of_research_id", foreignKey = @ForeignKey(name = "fk_research_article_id")))
    private List<LineOfResearch> linesOfResearch = new ArrayList<>();

    public Boolean addLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().add(lineOfResearch);
    }

    public Boolean removeLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().remove(lineOfResearch);
    }


    @PreUpdate
    @PrePersist
    public void generateSlug() {

        this.slug = createSlug(this.title, null);
    }

}
