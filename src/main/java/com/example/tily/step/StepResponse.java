package com.example.tily.step;

import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.til.Til;
import java.util.List;


public class StepResponse {
    public record CreateIndividualStepDTO(Long id) {
        public CreateIndividualStepDTO(Step step) {
            this(step.getId());
        }
    }

    public record FindReferenceDTO(Long id, String description, List<YoutubeDTO> youtubes, List<WebDTO> webs) {
        public FindReferenceDTO(Step step, List<YoutubeDTO> youtubeDTOs, List<WebDTO> webDTOs) {
            this(step.getId(), step.getDescription(), youtubeDTOs, webDTOs);
        }

        public record YoutubeDTO(Long id, String link) {}

        public record WebDTO(Long id, String link) {}
    }

    public record FindAllStepDTO(List<StepDTO> steps, int progress, String myRole) {

        public record StepDTO(Long id, String title, Boolean isSubmit, Long tilId) {
            public StepDTO(Step step, Til til) {
                this(step.getId(), step.getTitle(), til==null ? false : (til.getSubmitContent()!=null), til==null ? null : til.getId());
            }
        }
    }
}