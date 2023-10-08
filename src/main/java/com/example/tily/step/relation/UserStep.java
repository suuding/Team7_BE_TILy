package com.example.tily.step.relation;

import com.example.tily.step.Step;
import com.example.tily.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_step_tb")
public class UserStep {
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
