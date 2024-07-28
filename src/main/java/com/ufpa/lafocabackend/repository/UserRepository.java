package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsUserByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.groups g WHERE g.name = :adminGroupName")
    long countUsersInGroup(String adminGroupName);

    Optional<User> findBySlug(String slug);
}
