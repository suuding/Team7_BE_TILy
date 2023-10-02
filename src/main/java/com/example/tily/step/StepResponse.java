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

        private List<YoutubeLink> youtubeLinks;
        private List<ReferenceLink> referenceLinks;

        public static class YoutubeLink {
            private Long id;
            private String link;

            public YoutubeLink(Long id, String link){
                this.id = id;
                this.link = link;
            }
        }

        public static class ReferenceLink {
            private Long id;
            private String link;

            public ReferenceLink(Long id, String link){
                this.id = id;
                this.link = link;
            }
        }

        public FindReferenceDTO(Step step, List<YoutubeLink> youtubeLinks, List<ReferenceLink> referenceLinks){
            this.id = step.getId();
            this.description = step.getDescription();
            this.youtubeLinks = youtubeLinks;
            this.referenceLinks = referenceLinks;
        }
    }
}