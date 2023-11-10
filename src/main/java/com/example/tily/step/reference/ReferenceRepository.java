package com.example.tily.step.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {

    List<Reference> findByStepId(Long stepId);

    @Modifying
    @Query("update Reference r SET r.isDeleted = true WHERE r.isDeleted = false AND r.id = :referenceId")
    void softDeleteReferenceById(@Param("referenceId")Long referenceId);

    @Modifying
    @Query("update Reference r SET r.isDeleted = true WHERE r.isDeleted = false AND r.step.id IN :stepIds")
    void softDeleteReferenceByStepIds(List<Long> stepIds); // 여러 step에 대한 reference 삭제

    @Modifying
    @Query("update Reference r SET r.isDeleted = true WHERE r.isDeleted = false AND r.step.id = :stepId")
    void softDeleteReferenceByStepId(Long stepId); // 하나의 step에 대한 reference 삭제
}