package com.denmit.userbalance.repository;

import com.denmit.userbalance.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :login")
    Optional<User> findByEmail(@Param("login") String login);

    @Query("SELECT u FROM User u JOIN u.phones p WHERE p.phone = :login")
    Optional<User> findByPhone(@Param("login") String login);

    @EntityGraph(attributePaths = {"emails", "phones", "account"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findById(Long id);

    boolean existsByEmails_Email(String email);

    boolean existsByPhones_Phone(String phone);
}
