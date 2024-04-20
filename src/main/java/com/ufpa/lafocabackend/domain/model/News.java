package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class News {

    @Id
    @EqualsAndHashCode.Include
    private String newsId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime newsDate;

    @Column(nullable = false)
    private String tags;

    @Column(nullable = false)
    private String content;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private NewsPhoto newsPhoto;

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
}
