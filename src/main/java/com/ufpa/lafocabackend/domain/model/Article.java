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
    private String title;
    private String slug;
    private String journal;
    private String url;

    @PreUpdate
    @PrePersist
    public void generateSlug() {

        this.slug = createSlug(this.title, null);
    }

}
