package com.example.tily.til;

import com.example.tily._core.errors.ExceptionCode;
import com.example.tily._core.errors.CustomException;

import com.example.tily.alarm.AlarmRepository;
import com.example.tily.comment.Comment;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Category;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.RoadmapResponse;
import com.example.tily.roadmap.relation.GroupRole;
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
    private final AlarmRepository alarmRepository;

    // til 생성하기
    @Transactional
    public TilResponse.CreateTilDTO createTil(TilRequest.CreateTilDTO requestDTO, User user) {

        Long roadmapId = requestDTO.roadmapId();
        Long stepId = requestDTO.stepId();

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        // step이 roadmap에 속했는지 확인
        if (!step.getRoadmap().equals(roadmap))
            throw new CustomException(ExceptionCode.STEP_NOT_BELONG);

        getUserBelongRoadmap(roadmapId, user.getId());

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

    // til 조회하기
    public TilResponse.ViewDTO viewTil (Long tilId, User user) {

        Til til = getTilById(tilId);

        Step step = til.getStep();

        // roadmap 에 속한 사람만 볼 수 있음 (roadmap에 속하면 userstep에 넣어짐) -> getUserBelongRoadmap 대신 사용 가능
        UserStep userStep = userStepRepository.findByUserIdAndStepId(user.getId(), step.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.TIL_VIEW_FORBIDDEN));

        Map<Comment, Boolean> maps = new HashMap<>();
        List<Comment> comments = commentRepository.findByTilId(tilId);
        for (Comment comment : comments) {
            maps.put(comment, user.getId().equals(comment.getWriter().getId()));
        }
        //commentRepository.findByTilId(tilId).stream().map(c -> maps.put(c, user.getId().equals(c.getWriter().getId())));

        List<TilResponse.ViewDTO.CommentDTO> commentDTOs = comments.stream()
                .map(c -> new TilResponse.ViewDTO.CommentDTO(c, maps.get(c))).collect(Collectors.toList());

        return new TilResponse.ViewDTO(step, til, userStep.getIsSubmit(), commentDTOs);
    }

    // til 수정하기 (저장하기)
    @Transactional
    public void updateTil(TilRequest.UpdateTilDTO requestDTO, Long tilId, User user) {

        Til til = getTilById(tilId);

        checkTilWriterEqualUser(til, user);

        til.updateContent(requestDTO.content());
    }

    // til 제출하기
    @Transactional
    public void submitTil(TilRequest.SubmitTilDTO requestDTO, Long tilId, User user) {

        Til til = getTilById(tilId);

        Step step = til.getStep();
        Roadmap roadmap = til.getRoadmap();

        checkTilWriterEqualUser(til, user);

        // 사용자가 로드맵에 속했는지 확인
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmap.getId(), user.getId());

        UserStep userstep = userStepRepository.findByUserIdAndStepId(user.getId(), step.getId())
                .orElseThrow(()-> new CustomException(ExceptionCode.TIL_SUBMIT_FORBIDDEN));

        // 이미 til을 제출한 경우
        if (userstep.getIsSubmit().equals(true))
            throw new CustomException(ExceptionCode.TIL_ALREADY_SUBMIT);

        // 제출 시간 지났는데 제출하는 경우
        LocalDateTime now = LocalDateTime.now();
        if (step.getDueDate()!=null && now.isAfter(step.getDueDate()))
            throw new CustomException(ExceptionCode.TIL_END_DUEDATE);

        til.submitTil(requestDTO.submitContent()); // 내용, 제출 내용 저장
        userstep.submit(); // 제출 여부(완료) 저장
        userRoadmap.updateProgress(calProgress(roadmap.getId(), user.getId())); // 진도율 저장
    }

    @Transactional
    public void deleteTil(Long tilId, User user) {
        Til til = getTilById(tilId);

        checkTilWriterEqualUser(til, user);

        // 1. Til과 연관된 Comment들을 삭제한다.
        List<Comment> comments = commentRepository.findByTilId(tilId);
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        commentRepository.softDeleteCommentsByIds(commentIds);

        // 2. Comment들과 관련된 Alarm 삭제
        alarmRepository.deleteByCommentIds(commentIds);

        // 3. Til을 삭제한다.
        tilRepository.softDeleteTilById(tilId);
    }

    // 나의 til 목록 전체 조회하기
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

    //  로드맵의 특정 step의 til 목록 조회하기
    @Transactional
    public RoadmapResponse.FindTilOfStepDTO findTilOfStep(Long stepId, boolean isSubmit, boolean isMember, String name){

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        Roadmap roadmap = step.getRoadmap();

        // 특정 로드맵에 속한 UserRoadmap list
        List<UserRoadmap> userRoadmaps = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(roadmap.getId());
        // 특정 step에 대해 제출 여부, 사용자 이름으로 user 조회
        List<User> users = userStepRepository.findAllByStepIdAndIsSubmitAndName(stepId, isSubmit, name)
                .stream().map(UserStep::getUser).toList();

        List<RoadmapResponse.FindTilOfStepDTO.MemberDTO> members = new ArrayList<>();

        if (isMember) { // 로드맵에 속한 member만 대해
            for (User user : users) {
                // 로드맵에서의 사용자의 role을 알기 위해 사용자의 userRoadmap 조회
                Optional<UserRoadmap> userRoadmap = userRoadmaps.stream().filter(u -> u.getUser().equals(user)).findFirst();

                if (userRoadmap.isPresent() && userRoadmap.get().getRole().equals(GroupRole.ROLE_MEMBER.getValue())) {
                    Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
                    if (til==null) members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, user));
                    else members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(til, user));
                }
            }
        } else { // 로드맵에 속한 모든 사용자에 대해
            for (User user : users) {
                Til til = tilRepository.findByStepIdAndUserId(stepId, user.getId());
                if (til==null) members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(null, user));
                else members.add(new RoadmapResponse.FindTilOfStepDTO.MemberDTO(til, user));
            }
        }

        return new RoadmapResponse.FindTilOfStepDTO(members);
    }

    private Til getTilById(Long tilId) {
        return tilRepository.findById(tilId).orElseThrow(() -> new CustomException(ExceptionCode.TIL_NOT_FOUND));
    }

    private void checkTilWriterEqualUser(Til til, User user) {
        if (!til.getWriter().getId().equals(user.getId()))
            throw new CustomException(ExceptionCode.TIL_FORBIDDEN);
    }

    // 사용자가 로드맵에 속했는지
    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
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
