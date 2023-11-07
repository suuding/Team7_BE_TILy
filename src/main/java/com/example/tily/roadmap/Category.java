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

    public String getString(Category category) {
        return this.value;
    }

    public static Category getCategory(String category) {
        if (category.equals("individual"))
            return CATEGORY_INDIVIDUAL;
        else if (category.equals("group"))
            return CATEGORY_GROUP;
        else
            return CATEGORY_TILY;
    }
}
