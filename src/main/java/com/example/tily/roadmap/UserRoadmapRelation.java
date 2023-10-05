package com.example.tily.roadmap;

import com.example.tily.user.Role;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="userRoadmapRelation_tb")
public class UserRoadmapRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Roadmap roadmap;

    @ManyToOne
    User user;

    @Column
    private String content;
    @Column
    private Boolean isAccept;
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private int progress;
}
