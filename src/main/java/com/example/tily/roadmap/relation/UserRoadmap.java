package com.example.tily.roadmap.relation;

import com.example.tily.BaseTimeEntity;
import com.example.tily.roadmap.Roadmap;
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
@Table(name="user_roadmap_tb")
@SQLDelete(sql = "UPDATE user_roadmap_tb SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
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
    private boolean isAccept;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private int progress;

    @Column
    private boolean isDeleted = false;

    @Builder
    public UserRoadmap(Roadmap roadmap, User user, String content, boolean isAccept, GroupRole role, int progress) {
        this.roadmap = roadmap;
        this.user = user;
        this.content = content;
        this.isAccept = isAccept;
        this.role = role.getValue();
        this.progress = progress;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateRoleAndIsAccept(String role, boolean isAccept) {
        this.role = role;
        this.isAccept = isAccept;
    }

    public void updateIsAccept(boolean isAccept) { this.isAccept = isAccept; }

    public void updateProgress(int progress) {
        this.progress = progress;
    }
}
