package com.example.tily.comment;

import javax.validation.constraints.NotBlank;

public class CommentRequest {

    public record CreateCommentDTO(
            Long roadmapId,
            Long stepId,
            Long tilId,
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            String content
    ) { }

    public record UpdateCommentDTO(
            @NotBlank(message = "댓글 내용을 입력해주세요.")
            String content
    ) { }
}
