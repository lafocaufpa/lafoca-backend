package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    
    private String description;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime newsDate;
    
    @Column(nullable = false)
    private String tags;

    @Column(nullable = false)
    private String slug;

    @Column(nullable = false)
    private String content;

    @OneToOne
    private User user;

    public void createSlug(){

        if(this.tittle != null)
            this.slug = this.tittle.replaceAll("\\s", "-");
    }
}
