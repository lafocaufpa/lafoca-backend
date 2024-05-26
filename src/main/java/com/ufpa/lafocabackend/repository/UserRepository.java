package com.ufpa.lafocabackend.repository;

import com.ufpa.lafocabackend.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    /**
     *
     * @return se existir um usuário com o mesmo email, retorna true, caso contrário, retorna false
     */
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.groups g WHERE g.groupId = :groupId")
    long countUsersInGroup(Long groupId);

    Optional<User> findBySlug(String slug);
}
