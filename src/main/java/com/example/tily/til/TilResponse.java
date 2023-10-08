package com.example.tily.til;

import com.example.tily.roadmap.Roadmap;
import com.example.tily.step.Step;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TilResponse {

    @Getter @Setter
    public static class CreateTilDTO {
        private Long id;
        public CreateTilDTO(Til til){
            this.id = til.getId();
        }
    }

    @Getter @Setter
    public static class ViewDTO {
        private Long id;
        private Long stepId;
        private String stepTitle;
        private String content;
        private boolean isPersonal;

        public ViewDTO(Step step, Til til) {
            this.id = til.getId();
            this.stepId = step.getId();
            this.stepTitle = step.getTitle();
            this.content = til.getContent();
            this.isPersonal = til.isPersonal();
        }
    }

    @Getter @Setter
    public static class FindAllDTO {
        private List<TilDTO> tils;
        private Boolean hasNext;

        public FindAllDTO(Slice<Til> tils) {
            this.tils = tils.getContent().stream()
                    .map(til -> new TilDTO(til, til.getStep(), til.getRoadmap()))
                    .collect(Collectors.toList());
            this.hasNext = tils.hasNext();
        }

        @Getter @Setter
        public class TilDTO {
            private Long id;
            private String createDate;
            private StepDTO step;
            private RoadmapDTO roadmap;

            public TilDTO(Til til, Step step, Roadmap roadmap) {
                this.id = til.getId();
                this.createDate = til.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                this.step = new StepDTO(step);
                this.roadmap = new RoadmapDTO(roadmap);
            }

            @Getter @Setter
            public class StepDTO {
                private Long id;
                private String title;
                public StepDTO(Step step) {
                    this.id=step.getId();
                    this.title=step.getTitle();
                }
            }

            @Getter @Setter
            public class RoadmapDTO {
                private Long id;
                private String name;
                public RoadmapDTO(Roadmap roadmap) {
                    this.id= roadmap.getId();
                    this.name= roadmap.getName();
                }
            }
        }
    }
}
