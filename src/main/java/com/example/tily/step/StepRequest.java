package com.example.tily.step;

import javax.validation.constraints.NotBlank;

public class StepRequest {

    public record CreateIndividualStepDTO(@NotBlank(message = "스텝 제목을 입력해주세요.") String title) {
    }
}