package com.example.tily.til;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class TilRequest {

    @Getter @Setter
    public static class CreateTilDTO {
        @NotBlank(message = "TIL 제목을 입력해주세요.")
        private String title;
    }
}
