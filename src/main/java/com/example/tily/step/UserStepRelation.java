package com.example.tily.step;

import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="userStepRelation_tb")
public class UserStepRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Step step;

    @ManyToOne
    User user;

    @Column(nullable = false)
    private Boolean isSubmit;
}
