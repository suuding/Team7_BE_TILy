package com.example.tily.roadmap.relation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER("member"),
    ROLE_ADMIN("master");
    private String value;
}