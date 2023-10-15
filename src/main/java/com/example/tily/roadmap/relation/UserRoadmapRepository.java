package com.example.tily.roadmap.relation;

import com.example.tily.roadmap.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {

    @Query("select ur.roadmap from UserRoadmap ur where ur.user.id=:userId and (ur.isAccept=:isAccept or ur.isAccept=null)")
    List<Roadmap> findByUserId(@Param("userId") Long userId, @Param("isAccept") Boolean isAccept);

    List<UserRoadmap> findByRoadmapIdAndIsAcceptTrue(Long roadmapId);

    List<UserRoadmap> findByRoadmapIdAndIsAcceptFalse(Long roadmapId);

    Optional<UserRoadmap> findByRoadmapIdAndUserIdAndIsAcceptTrue(Long roadmapId, Long userId);

    Optional<UserRoadmap> findByRoadmapIdAndUserIdAndIsAcceptFalse(Long roadmapId, Long userId);

    @Query("select ur from UserRoadmap ur where ur.roadmap.id=:roadmapId and ur.user.id=:userId")
    Optional<UserRoadmap> findByRoadmapIdAndUserId(@Param("roadmapId") Long roadmapId,@Param("userId") Long userId);
}
