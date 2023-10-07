package com.example.tily.roadmap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Category {
    CATEGORY_INDIVIDUAL("individual"),
    CATEGORY_GROUP("group"),
    CATEGORY_TILY("tily");
    private String value;
}
