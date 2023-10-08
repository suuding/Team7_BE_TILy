package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

public class StepResponse {
    @Getter @Setter
    public static class CreateIndividualStepDTO{
        private Long id;
        public CreateIndividualStepDTO(Step step){
            this.id = step.getId();
        }
    }
}