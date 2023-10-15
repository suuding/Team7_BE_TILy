package com.example.tily.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

public class CommentRequest {

    @Getter @Setter
    public static class CreateCommentDTO{

        @NotBlank(message = "댓글 내용을 입력해주세요.")
        private String content;
    }

    @Getter @Setter
    public static class UpdateCommentDTO {
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        private String content;
    }
}
