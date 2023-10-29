package com.example.tily.comment;

import javax.validation.constraints.NotBlank;

public record CommentRequest(CreateCommentDTO createCommentDTO, UpdateCommentDTO updateCommentDTO) {

    public record CreateCommentDTO(@NotBlank(message = "댓글 내용을 입력해주세요.") String content) {
    }

    public record UpdateCommentDTO(@NotBlank(message = "댓글 내용을 입력해주세요.") String content) {
    }
}
