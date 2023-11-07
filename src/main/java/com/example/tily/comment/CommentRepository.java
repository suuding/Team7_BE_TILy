package com.example.tily.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.writer where c.til.id=:tilId")
    List<Comment> findByTilId(@Param("tilId") Long tilId);

    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.til.id IN :tilIds")
    void softDeleteAllCommentsByTilIds(List<Long> tilIds);

    @Modifying
    @Query("update Comment c SET c.isDeleted = true WHERE c.isDeleted = false AND c.id IN :commentIds")
    void softDeleteAllComments(List<Long> tilIds);
}
