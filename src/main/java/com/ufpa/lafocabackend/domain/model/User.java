package com.ufpa.lafocabackend.domain.model;

import com.ufpa.lafocabackend.core.security.dto.LoginRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.ufpa.lafocabackend.core.utils.LafocaUtils.createSlug;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String slug;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    String urlPhoto;

    @ManyToMany
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    public Boolean isLoginCorrect(LoginRequest login, PasswordEncoder encoder){
        return encoder.matches(login.password(), this.password);
    }

    @PrePersist
    @PreUpdate
    public void generateSlug() {

        this.slug = createSlug(this.name, this.userId);
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
