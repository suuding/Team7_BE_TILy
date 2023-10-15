package com.example.tily.step;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.reference.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByRoadmapId(Long id);

    @Query("select s from Step s join fetch s.roadmap where s.id=:id")
    Optional<Step> findById(Long id);
}
