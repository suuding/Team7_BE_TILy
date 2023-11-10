package com.example.tily.step;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {

    @Query("select s from Step s join fetch s.roadmap where s.id=:stepId")
    Optional<Step> findById(@Param("stepId") Long stepId);

    @Query("select s from Step s join fetch s.roadmap where s.roadmap.id=:roadmapId")
    List<Step> findByRoadmapId(@Param("roadmapId") Long roadmapId);

    @Modifying
    @Query("update Step s SET s.isDeleted = true WHERE s.isDeleted = false AND s.id = :stepId")
    void softDeleteStepById(Long stepId);

    @Modifying
    @Query("update Step s SET s.isDeleted = true WHERE s.isDeleted = false AND s.id IN :stepIds")
    void softDeleteStepByStepIds(List<Long> stepIds);
}
