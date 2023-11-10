package com.example.tily.comment;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public ResponseEntity<?> createComment(@RequestBody @Valid CommentRequest.CreateCommentDTO requestDTO,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse.CreateCommentDTO responseDTO = commentService.createComment(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, responseDTO));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId, @RequestBody @Valid CommentRequest.UpdateCommentDTO requestDTO,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(requestDTO, commentId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }
}