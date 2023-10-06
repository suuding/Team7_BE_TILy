package com.example.tily.roadmap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {

    @Query("select r from Roadmap r where r.creator.id =:userId and r.category=:category")
    List<Roadmap> findByUserId(@Param("userId") Long userId, @Param("category") Category category);
}
