package com.example.tily.til;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;

import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Category;
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
import java.time.format.DateTimeParseException;
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

        Roadmap roadmap = getRoadmapById(roadmapId);

        Step step = getStepById(stepId);

        if (!step.getRoadmap().equals(roadmap))
            throw new CustomException(ExceptionCode.STEP_NOT_INCLUDE);

        getUserIncludeRoadmap(roadmapId, user.getId());

        // 사용자가 이미 step에 대한 til을 생성한 경우
        Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
        if (til != null)
            throw new CustomException(ExceptionCode.TIL_STEP_EXIST);

        Til newTil = Til.builder()
                .roadmap(roadmap)
                .step(step)
                .title(step.getTitle())
                .writer(user)
                .commentNum(0)
                .isPersonal(roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL))
                .build();
        tilRepository.save(newTil);

        return new TilResponse.CreateTilDTO(newTil);
    }

    // til 저장하기
    @Transactional
    public void updateTil(TilRequest.UpdateTilDTO requestDTO, Long id, User user) {

        Til til = getTilById(id);

        if (checkTilWriterEqualUser(til, user))
            throw new CustomException(ExceptionCode.TIL_UPDATE_FORBIDDEN);

        til.updateContent(requestDTO.content());
    }

    public TilResponse.ViewDTO viewTil (Long roadmapId, Long stepId, Long tilId, User user) {

        Til til = getTilById(tilId);

        Step step = getStepById(stepId);

        Roadmap roadmap = getRoadmapById(roadmapId);

        // roadmap 에 속한 사람만 볼 수 있음 (roadmap에 속하면 userstep에 넣어짐) -> getUserIncludeRoadmap 대신 사용 가능
        UserStep userStep = userStepRepository.findByUserIdAndStepId(user.getId(), stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_VIEW_FORBIDDEN));

        Map<Comment, Boolean> maps = new HashMap<>();
        List<Comment> comments = commentRepository.findByTilId(tilId);
        for (Comment comment : comments) {
            maps.put(comment, user.getId().equals(comment.getWriter().getId()));
        }

        List<TilResponse.ViewDTO.CommentDTO> commentDTOs = comments.stream().map(c -> new TilResponse.ViewDTO.CommentDTO(c, maps.get(c))).collect(Collectors.toList());
        return new TilResponse.ViewDTO(step, til, userStep.getIsSubmit(), commentDTOs);
    }


    @Transactional
    public void submitTil(TilRequest.SubmitTilDTO requestDTO, Long roadmapId, Long stepId, Long tilId, User user) {

        Til til = getTilById(tilId);

        Step step = getStepById(stepId);

        Roadmap roadmap = getRoadmapById(roadmapId);

        if (checkTilWriterEqualUser(til, user))
            throw new CustomException(ExceptionCode.TIL_SUBMIT_FORBIDDEN);

        // 사용자가 그룹에 속했는지 확인
        UserRoadmap userRoadmap = getUserIncludeRoadmap(roadmapId, user.getId());

        // UserStep 조회 -> 사용자가 로드맵에 속했는지, 제출했는지 확인
        UserStep userstep = userStepRepository.findByUserIdAndStepId(user.getId(), stepId)
                .orElseThrow(()-> new CustomException(ExceptionCode.TIL_SUBMIT_FORBIDDEN));
        if (userstep.getIsSubmit().equals(true))
            throw new CustomException(ExceptionCode.TIL_ALREADY_SUBMIT);

        // 제출 시간 지났는데 제출하려고할때
        LocalDateTime now = LocalDateTime.now();
        if (step.getDueDate()!=null && now.isAfter(step.getDueDate()))
            throw new CustomException(ExceptionCode.TIL_END_DUEDATE);


        til.submitTil(requestDTO.submitContent()); // 내용, 제출 내용 저장
        userstep.submit(); // 제출 여부(완료) 저장
        userRoadmap.updateProgress(calProgress(roadmapId, user.getId())); // 진도율 저장
    }

    @Transactional
    public void deleteTil(Long tilId, User user) {
        Til til = getTilById(tilId);

        if (checkTilWriterEqualUser(til, user))
            throw new CustomException(ExceptionCode.TIL_DELETE_FORBIDDEN);

        tilRepository.deleteById(tilId);
    }

    public TilResponse.FindAllDTO findAllMyTil(Long roadmapId, String date, String title, int page, int size, User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Slice<Til> tils;

        if (date!=null) {
            LocalDate now = parseDate(date);
            LocalDateTime startDate = LocalDateTime.of(now, LocalTime.of( 0,0,0));
            LocalDateTime endDate = LocalDateTime.of(now, LocalTime.of( 23,59,59));

            tils = tilRepository.findAllByDateByOrderByCreatedDateDesc(user.getId(), roadmapId, startDate, endDate, title, pageable);
        } else {
            tils = tilRepository.findAllByOrderByCreatedDateDesc(user.getId(), roadmapId, title, pageable);
        }

        List<TilResponse.TilDTO> tilDTOs = tils.getContent().stream()
                .map(til -> new TilResponse.TilDTO(til, til.getStep(), til.getRoadmap())).collect(Collectors.toList());
        return new TilResponse.FindAllDTO(tilDTOs, tils.hasNext());
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }

    private Step getStepById(Long stepId) {
        return stepRepository.findById(stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));
    }

    private Til getTilById(Long tilId) {
        return tilRepository.findById(tilId).orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));
    }

    private boolean checkTilWriterEqualUser(Til til, User user) {
        return !til.getWriter().getId().equals(user.getId());
    }

    // 사용자가 로드맵에 속했는지
    private UserRoadmap getUserIncludeRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_ROADMAP_FORBIDDEN));
    }

    // 진도율 계산
    private int calProgress(Long roadmapId, Long userId) {

        List<UserStep> userSteps = userStepRepository.findByUserIdAndRoadmapId(userId, roadmapId);
        long sumNum = userSteps.size();
        long submitNum = userSteps.stream().filter(UserStep::getIsSubmit).count();

        return (int)(((double)submitNum/(double)sumNum)*100.0);
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new CustomException(ExceptionCode.DATE_WRONG);
        }
    }
}
