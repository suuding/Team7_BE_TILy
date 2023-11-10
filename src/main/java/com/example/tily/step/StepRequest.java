package com.example.tily.step;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class StepRequest {

    public record CreateIndividualStepDTO(
            @NotBlank(message = "스텝 제목을 입력해주세요.") String title
    ) { }

    public record CreateStepDTO(
            @NotBlank(message = "step의 제목을 입력해주세요.")
            String title,
            Long roadmapId,
            String description,
            LocalDateTime dueDate
    ) { }

    public record UpdateStepDTO(

            String title,
            String description,
            LocalDateTime dueDate
    ) { }
}