package com.example.tily.roadmap.relation;

import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {

    @Query("select ur.roadmap from UserRoadmap ur where ur.user.id=:userId")
    List<Roadmap> findByUserId(@Param("userId") Long userId);
}
