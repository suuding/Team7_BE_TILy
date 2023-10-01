package com.example.tily.roadmap;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RoadmapRequest {

    @Getter @Setter
    public static class CreateIndividualDTO {
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(min=2, max=20, message = "이름은 2자에서 20자 이내여야 합니다.")
        private String name;
    }
}
