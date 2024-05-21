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

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false, length = 4)
    private String year;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id", foreignKey = @ForeignKey(name = "fk_project_photo_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ProjectPhoto projectPhoto;

    @PreUpdate
    public void generateSlug() {
        this.slug = createSlug(this.title, this.projectId);
    }

    @PrePersist
    private void generateUUID() {
        this.projectId = UUID.randomUUID().toString();
        generateSlug();
    }

}
