package com.example.tily.roadmap;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="roadmap_tb")
public class Roadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String creator;
    @Column(nullable = false)
    private String category;
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
    private Long stepNum;

    @Builder
    public Roadmap(Long id, String creator, String category, String name, String description, Boolean isPublic, Long currentNum, String code, Boolean isRecruit, Long stepNum) {
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
    }
}