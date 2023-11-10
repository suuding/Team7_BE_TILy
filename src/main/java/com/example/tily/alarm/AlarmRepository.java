package com.example.tily.alarm;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select a from Alarm a " +
            "join fetch a.til " +
            "join fetch a.comment " +
            "where a.receiver.id=:receiverId and a.comment.writer.id!=:receiverId")
    List<Alarm> findAllByReceiverId(@Param("receiverId") Long receiverId, Sort sort);

    @Modifying
    void deleteByCommentId(Long commentId);

    @Modifying
    @Query("delete from Alarm a where a.comment.id in :commentIds")
    void deleteByCommentIds(List<Long> commentIds);
}
