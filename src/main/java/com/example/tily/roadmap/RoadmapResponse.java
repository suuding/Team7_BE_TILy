package com.example.tily.roadmap;

import lombok.Getter;
import lombok.Setter;

public class RoadmapResponse {
    @Getter @Setter
    public static class CreateIndividualDTO {
        private Long id;

        public CreateIndividualDTO(Roadmap roadmap)
        {
            this.id = roadmap.getId();
        }
    }

    @Getter @Setter
    public static class createGroupRoadmapDTO{
        private Long id;

        public createGroupRoadmapDTO(Roadmap roadmap){ this.id = roadmap.getId();}
    }
}
