package com.example.tily.til;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TilRepository extends JpaRepository<Til, Long>{

    @Query("select t from Til t where t.writer.id=:userId and t.roadmap.id=:roadmapId order by t.updatedDate desc")
    List<Til> findByUserIdByOrderByUpdatedDateDesc(@Param("roadmapId") Long roadmapId, @Param("userId") Long userId);

    @Query("select t from Til t join fetch t.writer where t.id=:id")
    Optional<Til> findById(Long id);

    @Query("select t from Til t " +
            "where t.writer.id=:userId " +
            "and (:roadmapId is null or t.roadmap.id=:roadmapId) " +
            "and (:title is null or t.title like %:title%)")
    Slice<Til> findAllByOrderByCreatedDateDesc(@Param("userId") Long userId,
                                               @Param("roadmapId") Long roadmapId,
                                               @Param("title") String title,
                                               Pageable pageable);
    @Query("select t from Til t " +
            "where t.writer.id=:userId " +
            "and (:roadmapId is null or t.roadmap.id=:roadmapId)" +
            "and (:startDate <= t.createdDate and t.createdDate<= :endDate)" +
            "and (:title is null or t.title like %:title%)")
    Slice<Til> findAllByDateByOrderByCreatedDateDesc(@Param("userId") Long userId,
                                               @Param("roadmapId") Long roadmapId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate,
                                               @Param("title") String title,
                                               Pageable pageable);

    List<Til> findByStepId(Long stepId);

    List<Til> findByWriterId(Long writerId);

    @Query("select t from Til t where t.writer.id=:userId and t.step.id=:stepId")
    Til findByStepIdAndUserId(@Param("stepId") Long stepId, @Param("userId") Long userId);

    @Query("SELECT t FROM Til t " +
            "where t.writer.id=:userId " +
            "and (:startDate <= t.createdDate and t.createdDate<= :endDate)")
    List<Til> findTilsByUserIdAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    Til findByRoadmapIdAndStepId(Long roadmapId, Long stepId);

    List<Til> findByRoadmapId(Long roadmapId);

    @Modifying
    @Query("update Til t SET t.isDeleted = true WHERE t.isDeleted = false AND t.id = :tilId")
    void softDeleteTilById(Long tilId);
  
    @Modifying
    @Query("update Til t SET t.isDeleted = true WHERE t.isDeleted = false AND t.id IN :tilIds")
    void softDeleteTilsByTilIds(List<Long> tilIds);
}
