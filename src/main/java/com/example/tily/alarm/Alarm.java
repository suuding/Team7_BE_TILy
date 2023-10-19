package com.example.tily.alarm;

import com.example.tily.BaseTimeEntity;
import com.example.tily.comment.Comment;
import com.example.tily.til.Til;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="alarm_tb")
public class Alarm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "til_id")
    private Til til;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    private Boolean isChecked;

    @Builder
    public Alarm(Til til, User receiver, Comment comment, Boolean isChecked) {
        this.til = til;
        this.receiver = receiver;
        this.comment = comment;
        this.isChecked = isChecked;
    }

    public void readAlarm() {
        this.isChecked = true;
    }
}
