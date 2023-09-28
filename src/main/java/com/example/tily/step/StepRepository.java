package com.example.tily.step;

import com.example.tily.roadmap.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByRoadmap(Roadmap roadmap); // 나중에 사용
}
