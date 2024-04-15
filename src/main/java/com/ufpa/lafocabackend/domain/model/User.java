package com.ufpa.lafocabackend.domain.model;

import com.ufpa.lafocabackend.core.security.dto.LoginRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @ManyToMany
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private UserPhoto userPhoto;

    @PrePersist
    private void setCodigo() {
        this.userId = UUID.randomUUID().toString();
    }

    public Boolean isLoginCorrect(LoginRequest login, PasswordEncoder encoder){
        return encoder.matches(login.password(), this.password);
    }

    public boolean addGroup (Group group){
        return groups.add(group);
    }

    public boolean removeGroup (Group group){
        return groups.remove(group);
    }
}
