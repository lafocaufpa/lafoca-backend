package com.ufpa.lafocabackend.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(columnDefinition = "datetime", nullable = false)
    private OffsetDateTime dateRegister;

    @ManyToMany
    @JoinTable(name = "userGroup",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    public Boolean passwordIsEquals(String password){
        return this.password.equals(password);
    }

    public boolean addGroup (Group group){
        return groups.add(group);
    }

    public boolean removeGroup (Group group){
        return groups.remove(group);
    }
}
