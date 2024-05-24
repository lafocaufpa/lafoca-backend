package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class News {

    @Id
    @EqualsAndHashCode.Include
    private String newsId;

    @Column(nullable = false, length = 225)
    private String title;

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false, length = 225)
    private String description;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime newsDate;

    @Column(nullable = false, length = 15000)
    private String content;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private NewsPhoto newsPhoto;

    @ManyToMany
    @JoinTable(name = "news_line_of_research",
            joinColumns = @JoinColumn(name = "news_id", foreignKey = @ForeignKey(name = "fk_news_research_id")),
            inverseJoinColumns = @JoinColumn(name = "line_of_research_id", foreignKey = @ForeignKey(name = "fk_research_news_id")))
    private List<LineOfResearch> linesOfResearch = new ArrayList<>();

    @PreUpdate
    public void generateSlug() {
        this.slug = createSlug(this.title, this.newsId);
    }

    @PrePersist
    private void generateUUID() {
        this.newsId = UUID.randomUUID().toString();
        generateSlug();
        createDescription();
    }

    public void createDescription() {
        if (this.content != null) {
            final String substring = StringUtils.substring(this.content, 0, 220);
            this.description = (substring.replaceAll("\\<.*?\\>", "")) + "...";
        }
    }

    public Boolean addLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().add(lineOfResearch);
    }

}
