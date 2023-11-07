package com.example.tily.til;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class TilRequest {

    public record CreateTilDTO(
            Long roadmapId,
            Long stepId,
            @NotBlank(message = "TIL 제목을 입력해주세요.") String title
    ) { }

    public record UpdateTilDTO(
            @NotBlank(message = "TIL 내용을 입력해주세요.") String content
    ) { }

    public record SubmitTilDTO(
            @NotBlank(message = "TIL 내용을 입력해주세요.") String submitContent
    ){ }
}
