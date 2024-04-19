package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {

    @Id
    @EqualsAndHashCode.Include
    private String projectId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String tittle;
    
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false)
    private String year;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ProjectPhoto projectPhoto;

    @PreUpdate
    public void generateSlug() {
        this.slug = createSlug(this.tittle, this.projectId);
    }

    @PrePersist
    private void generateUUID() {
        this.projectId = UUID.randomUUID().toString();
        generateSlug();
    }

}
