package com.example.tily.roadmap.relation;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_roadmap_tb")
public class UserRoadmap extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition="TEXT", length = 1000)
    private String content;

    @Column
    private Boolean isAccept;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private int progress;

    @Builder
    public UserRoadmap(Roadmap roadmap, User user, String content, Boolean isAccept, GroupRole role, int progress) {
        this.roadmap = roadmap;
        this.user = user;
        this.content = content;
        this.isAccept = isAccept;
        this.role = role.getValue();
        this.progress = progress;
    }

    public void updateRole(String role) { this.role = role; }

    public void updateIsAccept(Boolean isAccept) { this.isAccept = isAccept; }

    public void updateProgress(int progress) {
        this.progress = progress;
    }
}
