package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

public class StepResponse {
    @Getter @Setter
    public static class CreateStepDTO{
        private int id;
        public CreateStepDTO(Step step){
            this.id = step.getId();
        }
    }
}