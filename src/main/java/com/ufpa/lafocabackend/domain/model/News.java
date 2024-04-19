package com.ufpa.lafocabackend.domain.model;

import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String newsId;

    @Column(nullable = false)
    private String tittle;

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
    private NewsPhoto newsPhoto;

    @PostPersist
    @PostUpdate
    public void createSlug() {

        this.slug = LafocaUtils.createSlug(this.tittle, this.newsId);

    }

    @PrePersist
    @PreUpdate
    public void createDescription() {
        if (this.content != null) {
            final String substring = StringUtils.substring(this.content, 0, 220);
            this.description = (substring.replaceAll("\\<.*?\\>", "")) + "...";
        }
    }
}
