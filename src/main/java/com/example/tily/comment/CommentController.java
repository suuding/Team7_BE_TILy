package com.example.tily.comment;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
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

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id, @RequestBody @Valid CommentRequest.UpdateCommentDTO requestDTO,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(requestDTO, id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentService.deleteComment(id, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}