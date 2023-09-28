package com.example.tily.roadmap;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

public class RoadmapRequest {

    @Getter @Setter
    public static class CreateIndividualDTO {
        @NotNull
        private String name;
    }
}
