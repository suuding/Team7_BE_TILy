package com.example.tily.step.relation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStepRepository extends JpaRepository<UserStep, Long> {

    Optional<UserStep> findByUserIdAndStepId(Long userId, Long stepId);
}
