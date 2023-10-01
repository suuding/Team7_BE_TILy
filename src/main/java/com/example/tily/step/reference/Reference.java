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

    @ManyToOne
    private Step step;

    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String link;

    @Builder
    public Reference(Long id, String category, String link) {
        this.id = id;
        this.category = category;
        this.link = link;
    }
}