package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Project {

    @Id
    @EqualsAndHashCode.Include
    private String projectId;

    @ManyToMany
    @JoinTable(name = "project_line_of_research",
            joinColumns = @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_project_research_id")),
            inverseJoinColumns = @JoinColumn(name = "line_of_research_id", foreignKey = @ForeignKey(name = "fk_research_project_id")))
    private List<LineOfResearch> linesOfResearch = new ArrayList<>();

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String abstractText;

    @Column(nullable = false)
    private Boolean completed;

    @Column(nullable = false, length = 4)
    private String date;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id", foreignKey = @ForeignKey(name = "fk_project_photo_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ProjectPhoto projectPhoto;

    public Boolean addLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().add(lineOfResearch);
    }

    public Boolean removeLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().remove(lineOfResearch);
    }

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
