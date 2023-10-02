package com.example.tily.step;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.reference.Reference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByRoadmapId(Long id);
}
