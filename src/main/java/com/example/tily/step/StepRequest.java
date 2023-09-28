package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StepRequest {
    @Getter @Setter
    public static class CreateIndividualStepDTO{
        @NotBlank(message = "스텝 제목을 입력해주세요.")
        private String title;
    }
}