package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.Normalizer;
import java.time.OffsetDateTime;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long noticeId;

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

    @OneToOne
    private User user;

    public void createSlug() {

        if (this.tittle != null){
            this.slug = (this.tittle.replaceAll("\\s", "-")).toLowerCase();
            this.slug = Normalizer.normalize(this.slug, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
            this.slug = this.slug.replaceAll("\\?", "");
        }

    }

    public void createDescription() {
        if (this.content != null) {
            final String substring = StringUtils.substring(this.content, 0, 220);
            this.description = (substring.replaceAll("\\<.*?\\>", "")) + "...";
        }
    }
}
