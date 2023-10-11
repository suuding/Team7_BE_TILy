package com.example.tily.comment;

import com.example.tily.til.TilResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTilId(@Param("tilId") Long tilId);
}
