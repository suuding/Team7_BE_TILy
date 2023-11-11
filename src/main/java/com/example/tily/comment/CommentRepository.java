package com.example.tily.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Query("select c from Comment c join fetch c.writer where c.til.id=:tilId")
    List<Comment> findByTilId(@Param("tilId") Long tilId);

    List<Comment> findByWriterId(Long writerId);

    @Query("select c from Comment c where c.til.id in :tilIds")
    List<Comment> findByTilIds(@Param("tilIds") List<Long> tilIds);

    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.id = :commentId")
    void softDeleteCommentById(Long commentId);

    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.id IN :commentIds")
    void softDeleteCommentsByIds(List<Long> commentIds);

    // 추후 확장성을 고려한 soft delete 쿼리 작성
    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.til.id = :tilId")
    void softDeleteCommentsByTilId(Long tilId);

    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.til.id IN :tilIds")
    void softDeleteCommentsByTilIds(List<Long> tilIds);
}
