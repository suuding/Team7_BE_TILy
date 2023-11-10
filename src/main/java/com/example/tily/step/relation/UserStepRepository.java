package com.example.tily.step.relation;

import com.example.tily.roadmap.relation.UserRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("update UserStep us SET us.isDeleted = true WHERE us.isDeleted = false AND us.step.id IN :stepIds")
    void softDeleteUserStepByStepIds(List<Long> stepIds); // 여러 step들에 대한 UserStep 삭제

    @Modifying
    @Query("update UserStep us SET us.isDeleted = true WHERE us.isDeleted = false AND us.step.id = :stepId")
    void softDeleteUserStepByStepId(Long stepId); // 하나의 step에 대한 UserStep 삭제

    @Modifying
    @Query("update UserStep us SET us.isDeleted = true WHERE us.isDeleted = false AND us.id IN :userStepIds")
    void softDeleteUserStepByUserStepIds(List<Long> userStepIds);

    List<UserStep> findByUserId(Long userId);
}
