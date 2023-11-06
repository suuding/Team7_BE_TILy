package com.example.tily.step.reference;

import com.example.tily.step.Step;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="reference_tb")
@SQLDelete(sql = "UPDATE reference_tb SET isDeleted = true WHERE id = ?")
@Where(clause = "isDeleted = false")
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

    @Column
    private boolean isDeleted = false;

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