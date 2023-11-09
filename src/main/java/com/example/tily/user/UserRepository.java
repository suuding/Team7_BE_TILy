package com.example.tily.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query("update User u SET u.isDeleted = true WHERE u.isDeleted = false AND u.id = :userId")
    void softDeleteUserById(Long userId);

    Optional<User> findByEmail(String email);
}
