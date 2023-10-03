package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
}