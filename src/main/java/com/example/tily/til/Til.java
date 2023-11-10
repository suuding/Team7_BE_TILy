package com.example.tily.til;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.user.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="til_tb")
@SQLDelete(sql = "UPDATE til_tb SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
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

    @Column(columnDefinition="TEXT")
    private String content;

    @Column(columnDefinition="TEXT")
    private String submitContent;

    @Column
    private LocalDateTime submitDate;

    @Column
    private int commentNum;

    @Column
    private boolean isPersonal;

    @Column
    private boolean isDeleted = false;

    @Builder
    public Til(Long tilId, Roadmap roadmap, Step step, User writer, String title, String content, String submitContent, LocalDateTime submitDate, int commentNum, boolean isPersonal) {
        this.id = tilId;
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

    public void updateTitle (String title) {
        this.title = title;
    }

    public void addCommentNum () {
        this.commentNum++;
    }

    public void subCommentNum () {
        this.commentNum--;
    }

    public void submitTil(String submitContent) {
        this.content  = submitContent;
        this.submitContent = submitContent;
        this.submitDate = LocalDateTime.now();
    }
}
