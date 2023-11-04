package com.example.tily.comment;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.Exception400;
import com.example.tily._core.errors.exception.Exception403;
import com.example.tily._core.errors.exception.Exception404;
import com.example.tily.alarm.Alarm;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final TilRepository tilRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public CommentResponse.CreateCommentDTO createComment(CommentRequest.CreateCommentDTO requestDTO,
                                                          Long roadmapId, Long stepId, Long tilId, User user) {

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        Til til = tilRepository.findById(tilId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));


        String content = requestDTO.content();

        Comment comment = Comment.builder().roadmap(roadmap).step(step).writer(user).til(til).content(content).build();
        commentRepository.save(comment);

        // 댓글 작성하면 알림 생성
        Alarm alarm = Alarm.builder().til(til).receiver(til.getWriter()).comment(comment).isChecked(false).build();
        alarmRepository.save(alarm);

        return new CommentResponse.CreateCommentDTO(comment);
    }

    @Transactional
    public void updateComment(CommentRequest.UpdateCommentDTO requestDTO, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));


        if(!comment.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.COMMENT_UPDATE_FORBIDDEN);
        }

        comment.updateComment(requestDTO.content());
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        if(!comment.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.COMMENT_DELETE_FORBIDDEN);
        }

        alarmRepository.deleteByCommentId(id);
        commentRepository.deleteById(id);
    }
}
