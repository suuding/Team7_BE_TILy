package com.example.tily.comment;

import com.example.tily.til.Til;
import com.example.tily.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class CommentResponse {

    public record CreateCommentDTO(Long id) {
        public CreateCommentDTO(Comment comment) {
            this(comment.getId());
        }
    }

}
