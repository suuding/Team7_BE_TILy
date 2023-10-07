package com.example.tily.roadmap.relation;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.user.Role;
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
public class UserRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String content;
    @Column
    private Boolean isAccept;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private int progress;

    @Builder
    public UserRoadmap(Roadmap roadmap, User user, String content, Boolean isAccept, String role, int progress) {
        this.roadmap = roadmap;
        this.user = user;
        this.content = content;
        this.isAccept = isAccept;
        this.role = role;
        this.progress = progress;
    }
}
