package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, length = 2083, unique = true)
    private String url;

    @ManyToOne
    @JoinColumn(name = "line_of_research_id", foreignKey = @ForeignKey(name = "fk_article_research_id"))
    private LineOfResearch lineOfResearch;

    @PreUpdate
    @PrePersist
    public void generateSlug() {

        this.slug = createSlug(this.title, null);
    }

}
