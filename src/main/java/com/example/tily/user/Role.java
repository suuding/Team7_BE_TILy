package com.example.tily.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER("사용자"),
    ROLE_ADMIN("관리자");
    private String value;
}

