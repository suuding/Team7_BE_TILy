package com.example.tily.step.relation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserStepRepository extends JpaRepository<UserStep, Long> {

    Optional<UserStep> findByUserIdAndStepId(Long userId, Long stepId);

    List<UserStep> findByUserIdAndRoadmapId(Long userId, Long roadmapId);

    @Query("select us from UserStep us " +
            "where us.step.id=:stepId " +
            "and us.isSubmit=:isSubmit " +
            "and (:name is null or us.user.name like %:name%)")
    List<UserStep> findAllByStepIdAndIsSubmitAndName(@Param("stepId") Long stepId,
                                                     @Param("isSubmit") boolean isSubmit,
                                                     @Param("name") String name);

    Optional<UserStep> findByStepIdAndUserIdAndIsSubmitTrue(Long stepId, Long userId);
}
