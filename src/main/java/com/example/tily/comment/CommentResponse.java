package com.example.tily.comment;

import com.example.tily.til.Til;
import lombok.Getter;
import lombok.Setter;

public class CommentResponse {

    @Getter
    @Setter
    public static class CreateCommentDTO {
        private Long id;
        public CreateCommentDTO(Comment comment){
            this.id = comment.getId();
        }
    }
}
