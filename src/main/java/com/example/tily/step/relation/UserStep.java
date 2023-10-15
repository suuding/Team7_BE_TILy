package com.example.tily.step.relation;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_step_tb")
public class UserStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Boolean isSubmit;

    @Builder
    public UserStep(Roadmap roadmap, Step step, User user, Boolean isSubmit){
        this.roadmap = roadmap;
        this.step = step;
        this.user = user;
        this.isSubmit = isSubmit;
    }

    public void submit() {
        this.isSubmit = true;
    }
}
