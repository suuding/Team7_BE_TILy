package com.example.tily.comment;


public class CommentResponse {

    public record CreateCommentDTO(
            Long id
    ) {
        public CreateCommentDTO(Comment comment) {
            this(comment.getId());
        }
    }

}
