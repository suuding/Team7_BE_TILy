package com.example.tily.til;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TilRepository extends JpaRepository<Til, Long>{

    Til findFirstByOrderBySubmitDateDesc();

    Til findTilById(Long id);
    Optional<Til> findById(Long id);

    @Query("select t from Til t where t.writer.id=:userId " +
                                        "and (:roadmapId is null or t.roadmap.id=:roadmapId)" +
                                        "and (:title is null or t.title like %:title%)")
    Slice<Til> findAllByOrderByCreatedDateDesc(@Param("userId") Long userId,
                                               @Param("roadmapId") Long roadmapId,
                                               @Param("title") String title,
                                               Pageable pageable);

    @Query("select t from Til t where t.writer.id=:userId " +
            "and (:roadmapId is null or t.roadmap.id=:roadmapId)" +
            "and (:startDate <= t.createdDate and t.createdDate<= :endDate)" +
            "and (:title is null or t.title like %:title%)")
    Slice<Til> findAllByDateByOrderByCreatedDateDesc(@Param("userId") Long userId,
                                               @Param("roadmapId") Long roadmapId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("title") String title,
                                               Pageable pageable);
}
