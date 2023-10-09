package com.example.tily.step.relation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStepRepository extends JpaRepository<UserStep, Long> {
    List<UserStep> findByUser_Id(Long userId);

    List<UserStep> findByStep_Id(Long stepId);
}
