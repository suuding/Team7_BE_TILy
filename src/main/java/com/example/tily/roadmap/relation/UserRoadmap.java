package com.example.tily.roadmap.relation;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.user.Role;
import com.example.tily.user.User;
import lombok.AccessLevel;
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
    private Roadmap roadmap;

    @ManyToOne
    private User user;

    @Column
    private String content;
    @Column
    private Boolean isAccept;
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private int progress;
}
