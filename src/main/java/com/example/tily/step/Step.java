package com.example.tily.step;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.relation.UserStep;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="step_tb")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @Column(nullable = false)
    private String title;
    @Column
    private String description;
    @Column
    private LocalDateTime dueDate;

    @Builder
    public Step(Long id, Roadmap roadmap, String title, String description, LocalDateTime dueDate) {
        this.id = id;
        this.roadmap = roadmap;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public void update(String title, String description){
        this.title = title;
        this.description = description;
    }
}