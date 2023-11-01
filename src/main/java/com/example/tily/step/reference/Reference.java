package com.example.tily.step.reference;

import com.example.tily.step.Step;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="reference_tb")
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition="TEXT", length = 1000)
    private String link;

    @Builder
    public Reference(Step step, Long id, String category, String link) {
        this.step = step;
        this.id = id;
        this.category = category;
        this.link = link;
    }

    public void update(String link){
        this.link = link;
    }
}