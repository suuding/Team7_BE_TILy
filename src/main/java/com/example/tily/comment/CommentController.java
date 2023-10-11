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
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}/comments")
    public ResponseEntity<?> createComment(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId,
                                           @PathVariable("tilId") Long tilId ,
                                           @RequestBody @Valid CommentRequest.CreateCommentDTO requestDTO,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommentResponse.CreateCommentDTO responseDTO = commentService.createComment(requestDTO,
                roadmapId, stepId, tilId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    @PatchMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId,
                                       @PathVariable("tilId") Long tilId, @PathVariable("commentId") Long commentId,
                                       @RequestBody @Valid CommentRequest.UpdateCommentDTO requestDTO,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.updateComment(requestDTO, commentId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    @DeleteMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId")Long stepId,
                                       @PathVariable("tilId") Long tilId, @PathVariable("commentId") Long commentId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}