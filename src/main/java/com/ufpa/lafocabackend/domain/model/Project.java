package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.*;

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

    @Column(nullable = false, length = 10)
    private String date;

    @Column(length = 10)
    private String modality;

    @Column(length = 10)
    private String endDate;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "photo_id", foreignKey = @ForeignKey(name = "fk_project_photo_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ProjectPhoto projectPhoto;

    @ElementCollection
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    private Set<MemberInfo> members = new HashSet<>();

    public Boolean addLineOfResearch(LineOfResearch lineOfResearch) {
        return getLinesOfResearch().add(lineOfResearch);
    }

    @PreUpdate
    public void generateSlug() {
        this.slug = createSlug(this.title, this.projectId);
        sanitizeFields();
    }

    @PrePersist
    private void prePersist() {
        this.projectId = UUID.randomUUID().toString();
        generateSlug();
        sanitizeFields();
    }

    private void sanitizeFields() {
        sanitizeEndDate();
        sanitizeModality();
        sanitizeMembers();
    }

    private void sanitizeEndDate() {
        if (this.endDate != null && this.endDate.trim().isEmpty()) {
            this.endDate = null;
        }
    }

    private void sanitizeModality() {
        if (this.modality != null) {
            if (this.modality.trim().isEmpty()) {
                this.modality = null;
            } else {
                this.modality = StringUtils.capitalize(this.modality);
            }
        }
    }

    private void sanitizeMembers() {
        for (MemberInfo member : this.members) {
            member.sanitize();
        }
    }

    public boolean isOngoing() {
        return this.endDate == null || this.endDate.trim().isEmpty();
    }
}
