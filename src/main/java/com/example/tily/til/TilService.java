package com.example.tily.til;

import com.example.tily._core.errors.exception.Exception400;

import com.example.tily._core.errors.exception.Exception404;
import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily._core.errors.exception.Exception403;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


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

        Roadmap roadmap = roadmapRepository.findById(roadmapId).orElseThrow(
                () -> new Exception400("해당 로드맵을 찾을 수 없습니다")
        );

        Step step = stepRepository.findById(stepId).orElseThrow(
                () -> new Exception400("해당 스텝을 찾을 수 없습니다")
        );

        // 로드맵에 속한 step이 맞는지 확인
        if (!step.getRoadmap().equals(roadmap)) {
            throw new Exception400("현재 로드맵에는 해당 step이 존재하지 않습니다.");
        }

        // 사용자가 속하지 않은 로드맵에 til을 생성하려고 할때
        userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId()).orElseThrow(
                () -> new Exception403("해당 로드맵에 til을 생성할 권한이 없습니다.")
        );

        // 사용자가 이미 step에 대한 til을 생성한 경우
        Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
        if (til != null) {
            throw new Exception400("이미 해당 step에 대한 til이 존재합니다.");
        }

        String title = step.getTitle();
        Til newTil = Til.builder().roadmap(roadmap).step(step).title(title).writer(user).build();
        tilRepository.save(newTil);

        return new TilResponse.CreateTilDTO(newTil);
    }

    // til 저장하기
    @Transactional
    public void updateTil(TilRequest.UpdateTilDTO requestDTO, Long id, User user) {

        Til til = tilRepository.findById(id).orElseThrow(
                () -> new Exception400("해당 til을 찾을 수 없습니다.")
        );

        if (!til.getWriter().getId().equals(user.getId())) {
            throw new Exception403("해당 til을 저장할 권한이 없습니다.");
        }

        String content = requestDTO.getContent();
        if(content == null){
            throw new Exception400("TIL 내용을 입력해주세요.");
        }
        til.updateContent(content);
    }

    public TilResponse.ViewDTO viewTil(Long tilId, Long stepId, User user) {
        Til til = tilRepository.findById(tilId).orElseThrow(
                () -> new Exception400("해당 TIL을 찾을 수 없습니다. ")
        );
        Step step = stepRepository.findById(stepId).orElseThrow(
                () -> new Exception400("해당 스텝을 찾을 수 없습니다. ")
        );

        UserStep userStep = userStepRepository.findByUserIdAndStepId(til.getWriter().getId(), stepId).orElseThrow(
                () -> new Exception403("권한이 없습니다.")
        );
        Map<Comment, Boolean> maps = new HashMap<>();
        List<Comment> comments = commentRepository.findByTilId(tilId);
        for (Comment comment : comments) {
            maps.put(comment, user.getId().equals(comment.getWriter().getId()));
        }

        return new TilResponse.ViewDTO(step, til, userStep.getIsSubmit(), comments, maps);
    }


    @Transactional
    public void submitTil(TilRequest.SubmitTilDTO requestDTO, Long roadmapId, Long stepId, Long tilId, User user) {

        Til til = tilRepository.findById(tilId).orElseThrow(
                () -> new Exception400("해당 til을 찾을 수 없습니다.")
        );

        if (!Objects.equals(til.getWriter().getId(), user.getId())) {
            throw new Exception403("til을 제출할 권한이 없습니다.");
        }

        String submitContent = requestDTO.getSubmitContent();
        if(submitContent == null){
            throw new Exception400("TIL 내용을 입력해주세요.");
        }

        // 제출 내용을 저장 내용에도 저장
        til.submitTil(submitContent);

        // 제출 여부(완료) 저장
        UserStep userstep = userStepRepository.findByStepId(stepId);
        if (userstep.getIsSubmit().equals(true)) {
            throw new Exception400("이미 한번 제출하였습니다.");
        }
        userstep.submit();

        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserId(roadmapId, user.getId()).orElseThrow(
                () -> new Exception403("해당 로드맵에 속하지 않았습니다.")
        );
        int progress = calProgress(roadmapId, user.getId());

        userRoadmap.updateProgress(progress);
    }

    @Transactional
    public void deleteTil(Long id, User user) {
        Til til = tilRepository.findById(id).orElseThrow(
                () -> new Exception404("존재하지 않는 til입니다.")
        );

        if (!til.getWriter().equals(user)) {
            throw new Exception403("해당 til을 삭제할 권한이 없습니다.");
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
                throw new Exception400("입력한 날짜를 찾을 수 없습니다.");
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
