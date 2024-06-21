package com.ufpa.lafocabackend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String userId;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true, length = 500)
    private String slug;

    @Column(nullable = false, length = 255)
    private String password;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @Column(length = 40)
    private String photoUpdate;

    @Column( columnDefinition = "VARCHAR(700)")
    String urlPhoto;

    @ManyToMany
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_group_user_id")),
            inverseJoinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "fk_user_group_group_id")))
    private Set<Group> groups = new HashSet<>();

    public Boolean isLoginCorrect(String password, PasswordEncoder encoder){
        return encoder.matches(password, this.password);
    }

    @PreUpdate
    public void generateSlug() {
        this.slug = createSlug(this.name, this.userId);
    }

    @PrePersist
    private void generateUUID() {
        this.userId = UUID.randomUUID().toString();
        generateSlug();
    }

    public boolean addGroup (Group group){
        return groups.add(group);
    }

    public boolean removeGroup (Group group){
        return groups.remove(group);
    }

    public void removePhoto(){
        this.urlPhoto = null;
    }
}
