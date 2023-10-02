package com.example.tily.til;

import com.example.tily.step.Step;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="til_tb")
public class Til {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Step step;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column
    private String submitContent;

    @Column
    private LocalDateTime submitDate;

    @Column
    private int commentNum;

    @Column
    private boolean isPersonal;

    @Builder
    public Til(Long id, Step step, String title, String content, String submitContent, LocalDateTime submitDate, int commentNum, boolean isPersonal) {
        this.id = id;
        this.step = step;
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

}
