package com.ufpa.lafocabackend.domain.model;

import com.ufpa.lafocabackend.core.utils.LafocaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tcc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long tccId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 500, unique = true)
    private String slug;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(700)")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String abstractText;

    private String nameMember;

    private String slugMember;

    @ManyToMany
    @JoinTable(name = "tcc_line_of_research",
            joinColumns = @JoinColumn(name = "tcc_id", foreignKey = @ForeignKey(name = "fk_tcc_research_id")),
            inverseJoinColumns = @JoinColumn(name = "line_of_research_id", foreignKey = @ForeignKey(name = "fk_research_tcc_id")))
    private List<LineOfResearch> linesOfResearch = new ArrayList<>();

    @PreUpdate
    @PrePersist
    public void preprocess() {
        generateSlug();
        normalizeUrl();
    }

    private void generateSlug() {
        this.slug = createSlug(this.title, date.toString());
    }

    private void normalizeUrl() {
        if (this.url != null) {
            this.url = LafocaUtils.normalizeUrl(this.url);
        }
    }

}
