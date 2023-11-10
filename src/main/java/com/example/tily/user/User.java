package com.example.tily.user;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.step.relation.UserStep;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_tb")
@SQLDelete(sql = "UPDATE user_tb SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String password;

    @Column
    private String image;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private boolean isDeleted = false;

    @Builder
    public User(Long userId, String email, String name, String password, String image, Role role) {
        this.id = userId;
        this.email = email;
        this.name = name;
        this.password = password;
        this.image = image;
        this.role = role;
    }

    public void updatePassword (String password) {
        this.password  = password;
    }

    public void updateImage (String image) {this.image = image; }
}