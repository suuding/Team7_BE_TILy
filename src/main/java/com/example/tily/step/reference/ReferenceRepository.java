package com.example.tily.step.reference;

import com.example.tily.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    List<Reference> findByStepId(Long stepId);

    @Modifying
    @Query("update Reference r SET r.isDeleted = true WHERE r.isDeleted = false AND r.id IN :referenceIds")
    void softDeleteAllReferences(List<Long> referenceIds);
}