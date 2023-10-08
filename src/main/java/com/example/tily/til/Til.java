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

    private String title;

    private String content;

    private String submitContent;

    private LocalDateTime submitDate;

    private int commentNum;

    @Builder
    public Til(Long id, Step step, String title, String content, String submitContent, LocalDateTime submitDate, int commentNum) {
        this.id = id;
        this.step = step;
        this.title = title;
        this.content = content;
        this.submitContent = submitContent;
        this.submitDate = submitDate;
        this.commentNum = commentNum;
    }

}
