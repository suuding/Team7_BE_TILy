package com.example.tily.til;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;

import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TilService {
    private final TilRepository tilRepository;
    private final StepRepository stepRepository;
    private final RoadmapRepository roadmapRepository;
    private final CommentRepository commentRepository;
    private final UserStepRepository userStepRepository;
    private final UserRoadmapRepository userRoadmapRepository;

    // til 생성하기
    @Transactional
    public TilResponse.CreateTilDTO createTil(TilRequest.CreateTilDTO requestDTO, Long roadmapId, Long stepId, User user) {

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
          .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Step step = stepRepository.findById(stepId)
          .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        // 로드맵에 속한 step이 맞는지 확인
        if (!step.getRoadmap().equals(roadmap)) {
            throw new CustomException(ExceptionCode.STEP_NOT_FOUND);
        }

        // 사용자가 속하지 않은 로드맵에 til을 생성하려고 할때
        userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId())
          .orElseThrow(() -> new CustomException(ExceptionCode.TIL_ROADMAP_FORBIDDEN));

        // 사용자가 이미 step에 대한 til을 생성한 경우
        Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
        if (til != null) {
            throw new CustomException(ExceptionCode.TIL_STEP_EXIST);
        }

        String title = step.getTitle();
        Til newTil = Til.builder().roadmap(roadmap).step(step).title(title).writer(user).build();
        tilRepository.save(newTil);

        return new TilResponse.CreateTilDTO(newTil);
    }

    // til 저장하기
    @Transactional
    public void updateTil(TilRequest.UpdateTilDTO requestDTO, Long id, User user) {

        Til til = tilRepository.findById(id)
          .orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));

        if (!til.getWriter().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.TIL_UPDATE_FORBIDDEN);
        }

        String content = requestDTO.content();
        if(content == null){
            throw new CustomException(ExceptionCode.TIL_CONTENT_NULL);
        }
        til.updateContent(content);
    }

    public TilResponse.ViewDTO viewTil (Long tilId, Long stepId, User user) {
        Til til = tilRepository.findById(tilId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        UserStep userStep = userStepRepository.findByUserIdAndStepId(til.getWriter().getId(), stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_VIEW_FORBIDDEN));


        Map<Comment, Boolean> maps = new HashMap<>();
        List<Comment> comments = commentRepository.findByTilId(tilId);
        for (Comment comment : comments) {
            maps.put(comment, user.getId().equals(comment.getWriter().getId()));
        }

        return new TilResponse.ViewDTO(step, til, userStep.getIsSubmit(), comments, maps);
    }


    @Transactional
    public void submitTil(TilRequest.SubmitTilDTO requestDTO, Long roadmapId, Long stepId, Long tilId, User user) {

        Til til = tilRepository.findById(tilId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));

        if (!Objects.equals(til.getWriter().getId(), user.getId())) {
            throw new CustomException(ExceptionCode.TIL_SUBMIT_FORBIDDEN);
        }

        String submitContent = requestDTO.submitContent();
        if(submitContent == null){
            throw new CustomException(ExceptionCode.TIL_CONTENT_NULL);
        }

        // 제출 내용을 저장 내용에도 저장
        til.submitTil(submitContent);

        // 제출 여부(완료) 저장
        UserStep userstep = userStepRepository.findByStepId(stepId);
        if (userstep.getIsSubmit().equals(true)) {
            throw new CustomException(ExceptionCode.TIL_ALREADY_SUBMIT);
        }
        userstep.submit();

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserId(roadmapId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_SUBMIT_FORBIDDEN));

        int progress = calProgress(roadmapId, user.getId());
        userRoadmap.updateProgress(progress);
    }

    @Transactional
    public void deleteTil(Long id, User user) {
        Til til = tilRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));

        if (!til.getWriter().equals(user)) {
            throw new CustomException(ExceptionCode.TIL_DELETE_FORBIDDEN);
        }

        tilRepository.deleteById(id);
    }

    @Transactional
    public TilResponse.FindAllDTO findAllMyTil(Long roadmapId, String date, String title, int page, int size, User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        if (date!=null) {
            try {
                LocalDate now = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDateTime startDate = LocalDateTime.of(now, LocalTime.of( 0,0,0));
                LocalDateTime endDate = LocalDateTime.of(now, LocalTime.of( 23,59,59));

                Slice<Til> tils = tilRepository.findAllByDateByOrderByCreatedDateDesc(user.getId(), roadmapId, startDate, endDate, title, pageable);
                return new TilResponse.FindAllDTO(tils);
            } catch (Exception e) {
                throw new CustomException(ExceptionCode.DATE_WRONG);
            }
        } else {
            Slice<Til> tils = tilRepository.findAllByOrderByCreatedDateDesc(user.getId(), roadmapId, title, pageable);
            return new TilResponse.FindAllDTO(tils);
        }
    }

    public int calProgress(Long roadmapId, Long userId) {

        List<UserStep> userSteps = userStepRepository.findByUserIdAndRoadmapId(userId, roadmapId);
        int sumNum = userSteps.size();
        int submitNum = 0;
        for (UserStep userStep : userSteps) {
            if (userStep.getIsSubmit())  submitNum++;
        }
        return (int)(((double)submitNum/(double)sumNum)*100.0);
    }
}
