package com.example.tily.roadmap;

import com.example.tily.BaseTimeEntity;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="roadmap_tb")
@SQLDelete(sql = "UPDATE roadmap_tb SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Roadmap extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition="TEXT", length = 1000)
    private String description;

    @Column
    private boolean isPublic;

    @Column
    private String code;

    @Column
    private boolean isRecruit;

    @Column
    private int stepNum;

    @Column
    private String image;

    @Column
    private boolean isDeleted = false;

    @Builder
    public Roadmap(Long roadmapId, User creator, Category category, String name, String description, boolean isPublic, String code, boolean isRecruit, int stepNum, String image) {
        this.id = roadmapId;
        this.creator = creator;
        this.category = category;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.code = code;
        this.isRecruit = isRecruit;
        this.stepNum = stepNum;
        this.image = image;
    }

    public void update(RoadmapRequest.UpdateRoadmapDTO roadmap){
        this.name = roadmap.name();
        this.description = roadmap.description();
        this.isPublic = roadmap.isPublic();
        this.isRecruit = roadmap.isRecruit();
    }

    public void addStepNum() {
        this.stepNum++;
    }

    public void subStepNum() {
        this.stepNum--;
    }

    public void updateImage (String image) {this.image = image; }
}