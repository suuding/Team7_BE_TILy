package com.example.tily.roadmap;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="roadmap_tb")
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
    @Column
    private String description;
    @Column
    private Boolean isPublic;
    @Column
    private Long currentNum;
    @Column
    private String code;
    @Column
    private Boolean isRecruit;
    @Column
    private int stepNum;
    @Column
    private String image;

    @Builder
    public Roadmap(Long id, User creator, Category category, String name, String description, Boolean isPublic, Long currentNum, String code, Boolean isRecruit, int stepNum, String image) {
        this.id = id;
        this.creator = creator;
        this.category = category;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.currentNum = currentNum;
        this.code = code;
        this.isRecruit = isRecruit;
        this.stepNum = stepNum;
        this.image = image;
    }

    public void update(String name, String description, String code, Boolean isPublic, Boolean isRecruit){
        this.name = name;
        this.description = description;
        this.code = code;
        this.isPublic = isPublic;
        this.isRecruit = isRecruit;
    }

    public void updateImage (String image) {this.image = image; }
}