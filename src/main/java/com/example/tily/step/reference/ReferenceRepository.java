package com.example.tily.step.reference;

import com.example.tily.step.Step;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    List<Reference> findByStepId(Long stepId);
}
