package com.example.tily.comment;

import com.example.tily._core.utils.ApiUtils;
import com.example.tily.til.TilRequest;
import com.example.tily.til.TilResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}/tils/{tilId}/comments")
    public ResponseEntity<?> createComment(@PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId, @PathVariable("tilId") Long tilId ,@RequestBody @Valid CommentRequest.CreateCommentDTO requestDTO) {
        CommentResponse.CreateCommentDTO responseDTO = commentService.createComment(requestDTO, roadmapId, stepId, tilId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}
