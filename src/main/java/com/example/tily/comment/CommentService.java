package com.example.tily.comment;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.alarm.Alarm;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.alarm.AlarmResponse;
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

import java.util.Optional;

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

        Roadmap roadmap = roadmapRepository.findById(roadmapId).orElseThrow(
                () -> new Exception400("해당 로드맵을 찾을 수 없습니다")
        );

        Step step = stepRepository.findById(stepId).orElseThrow(
                () -> new Exception400("해당 스텝을 찾을 수 없습니다")
        );

        Til til = tilRepository.findById(tilId).orElseThrow(
                () -> new Exception400("해당 til을 찾을 수 없습니다")
        );

        String content = requestDTO.getContent();

        Comment comment = Comment.builder().roadmap(roadmap).step(step).writer(user).til(til).content(content).build();
        commentRepository.save(comment);

        // 댓글 작성하면 알림 생성
        Alarm alarm = Alarm.builder().til(til).receiver(til.getWriter()).comment(comment).isChecked(false).build();
        alarmRepository.save(alarm);

        return new CommentResponse.CreateCommentDTO(comment);
    }

    @Transactional
    public void updateComment(CommentRequest.UpdateCommentDTO requestDTO, Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new Exception400("해당 댓글을 찾을 수 없습니다")
        );
        if(comment.getWriter().getId() != user.getId()) {
            throw new Exception400("해당 댓글을 수정할 권한이 없습니다.");
        }

        String content = requestDTO.getContent();
        if(content == null){
            throw new Exception400("댓글 내용을 입력해주세요.");
        }
        comment.updateComment(content);
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new Exception400("해당 댓글을 찾을 수 없습니다")
        );

        if(comment.getWriter().getId() != user.getId()) {
            throw new Exception400("해당 댓글을 삭제할 권한이 없습니다.");
        }
        commentRepository.deleteById(id);
    }
}
