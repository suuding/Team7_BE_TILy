package com.example.tily.step;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class StepRequest {

    public record CreateIndividualStepDTO(@NotBlank(message = "스텝 제목을 입력해주세요.") String title) {
    }
}