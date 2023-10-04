package com.example.tily.til;

import com.example.tily.step.Step;
import lombok.Getter;
import lombok.Setter;

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
}
