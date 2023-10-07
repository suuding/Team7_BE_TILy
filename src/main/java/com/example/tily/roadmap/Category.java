package com.example.tily.roadmap;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily._core.errors.exception.Exception404;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
