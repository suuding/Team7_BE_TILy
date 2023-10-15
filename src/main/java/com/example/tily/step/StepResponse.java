package com.example.tily.step;

import com.example.tily.til.Til;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StepResponse {
    @Getter @Setter
    public static class CreateIndividualStepDTO{
        private Long id;
        public CreateIndividualStepDTO(Step step){
            this.id = step.getId();
        }
    }

    @Getter @Setter
    public static class FindReferenceDTO{
        private Long id;
        private String description;

        private List<YoutubeDTO> youtubes;
        private List<WebDTO> references;

        @Getter @Setter
        public static class YoutubeDTO {
            private Long id;
            private String link;

            public YoutubeDTO(Long id, String link){
                this.id = id;
                this.link = link;
            }
        }

        @Getter @Setter
        public static class WebDTO {
            private Long id;
            private String link;

            public WebDTO(Long id, String link){
                this.id = id;
                this.link = link;
            }
        }

        public FindReferenceDTO(Step step, List<YoutubeDTO> youtubeLinks, List<WebDTO> referenceLinks){
            this.id = step.getId();
            this.description = step.getDescription();
            this.youtubes = youtubeLinks;
            this.references = referenceLinks;
        }
    }

    @Getter @Setter
    public static class FindAllStepDTO {
        private List<StepDTO> steps;
        private int progress;
        private String role;

        public FindAllStepDTO(List<Step> steps, Map<Step, Til> maps, int progress, String role) {
            this.steps = steps.stream().map(step -> new StepDTO(step, maps.get(step))).collect(Collectors.toList());
            this.progress = progress;
            this.role = role;
        }

        @Getter @Setter
        public class StepDTO {
            private Long id;
            private String title;
            private Boolean isCompleted;
            private Long tillId;

            public StepDTO(Step step, Til til) {
                this.id = step.getId();
                this.title = step.getTitle();
                this.isCompleted = til==null ? false : true;
                this.tillId = til.getId();
            }
        }
    }
}