package com.example.tily.til;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="til_tb")
public class Til extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="step_id")
    private Step step;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User writer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition="TEXT", length = 5000)
    private String content;

    @Column(columnDefinition="TEXT", length = 5000)
    private String submitContent;

    @Column
    private LocalDateTime submitDate;

    @Column
    private int commentNum;

    @Column
    private boolean isPersonal;


    @Builder
    public Til(Long id, Roadmap roadmap, Step step, User writer, String title, String content, String submitContent, LocalDateTime submitDate, int commentNum, boolean isPersonal) {
        this.id = id;
        this.roadmap = roadmap;
        this.step = step;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.submitContent = submitContent;
        this.submitDate = submitDate;
        this.commentNum = commentNum;
        this.isPersonal = isPersonal;
    }

    public void updateContent (String content) {
        this.content  = content;
    }

    public void submitTil(String submitContent) {
        this.content  = submitContent;
        this.submitContent = submitContent;
        this.submitDate = LocalDateTime.now();
    }
}
