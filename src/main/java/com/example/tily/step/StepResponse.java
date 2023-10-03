package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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
        private List<ReferenceDTO> references;

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
        public static class ReferenceDTO {
            private Long id;
            private String link;

            public ReferenceDTO(Long id, String link){
                this.id = id;
                this.link = link;
            }
        }

        public FindReferenceDTO(Step step, List<YoutubeDTO> youtubeLinks, List<ReferenceDTO> referenceLinks){
            this.id = step.getId();
            this.description = step.getDescription();
            this.youtubes = youtubeLinks;
            this.references = referenceLinks;
        }
    }
}