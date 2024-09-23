package com.ufpa.lafocabackend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "access_group",
        uniqueConstraints = {
        @UniqueConstraint(name = "uk_name", columnNames = {"name"})
}
)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long groupId;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name = "permission_group",
            joinColumns = @JoinColumn(
                    name = "group_id",
                    foreignKey = @ForeignKey(name = "fk_permission_group_group_id")),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id",
                    foreignKey = @ForeignKey(name = "fk_permission_group_permission_id")))
    private Set<Permission> permissions = new HashSet<>();

    @Column(nullable = false)
    private String description;

    public Group() {

    }

    public boolean associatePermission(Permission Permission) {
        return permissions.add(Permission);
    }

    public void disassociatePermission(Permission Permission) {
        permissions.remove(Permission);
    }
}