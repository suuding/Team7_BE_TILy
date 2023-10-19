package com.example.tily.comment;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
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
@Table(name="comment_tb")
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "til_id")
    private Til til;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User writer;

    private String content;



    @Builder
    public Comment (Long id, Roadmap roadmap, Step step, Til til, User writer, String content) {
        this.id = id;
        this.til = til;
        this.writer = writer;
        this.content = content;
    }

    public void updateComment (String content) {
        this.content  = content;
    }
}
