package com.example.tily.step;

import com.example.tily._core.errors.ExceptionCode;
import com.example.tily._core.errors.CustomException;
import com.example.tily.alarm.AlarmRepository;
import com.example.tily.comment.Comment;
import com.example.tily.roadmap.Category;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStep;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StepService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserStepRepository userStepRepository;
    private final CommentRepository commentRepository;
    private final ReferenceRepository referenceRepository;
    private final AlarmRepository alarmRepository;

    // step 생성하기
    @Transactional
    public StepResponse.CreateStepDTO createStep(StepRequest.CreateStepDTO requestDTO, User user) {

        Long roadmapId = requestDTO.roadmapId();
        Roadmap roadmap = getRoadmapById(roadmapId);

        checkMasterAndManagerPermission(roadmapId, user);

        // 사용자가 해당 로드맵에 속했는지 확인
        userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_ROADMAP_FORBIDDEN));

        Step step = Step.builder()
                .roadmap(roadmap)
                .title(requestDTO.title())
                .description(requestDTO.description())
                .dueDate(requestDTO.dueDate()!=null ? requestDTO.dueDate().plusHours(9) : null)
                .build(); // 개인 로드맵이므로 description, dueDate 는 null
        stepRepository.save(step);

        List<User> users = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(roadmapId).stream().map(UserRoadmap::getUser).toList();
        for (User u : users) {
            UserStep userStep = UserStep.builder()
                    .roadmap(roadmap)
                    .step(step)
                    .user(u)
                    .isSubmit(roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL))
                    .build();
            userStepRepository.save(userStep);
        }

        roadmap.addStepNum();

        return new StepResponse.CreateStepDTO(step);
    }

    // step 수정하기
    @Transactional
    public void updateStep(Long stepId, StepRequest.UpdateStepDTO requestDTO, User user) {

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        Roadmap roadmap = step.getRoadmap();

        checkMasterAndManagerPermission(roadmap.getId(), user); // 관리자 권한 확인

        // 개인 로드맵이면 대응되는 til의 제목도 수정
        if (roadmap.getCategory().equals(Category.CATEGORY_INDIVIDUAL)) {
            Til til = tilRepository.findByRoadmapIdAndStepId(roadmap.getId(), stepId);
            if (til != null) til.updateTitle(requestDTO.title());
            step.updateTitle(requestDTO.title());
        } else { // 그룹 로드맵일 때
            step.update(requestDTO.title(), requestDTO.description(), requestDTO.dueDate()!=null ? requestDTO.dueDate().plusHours(9) : null);
        }
    }

    // 특정 로드맵의 step 목록 전체 조회
    public StepResponse.FindAllStepDTO findAllStep (Long roadmapId, User user) {
        getRoadmapById(roadmapId);

        List<Step> steps = stepRepository.findByRoadmapId(roadmapId);

        Map<Step, Til> maps = new HashMap<>();
        for (Step step : steps) {
            Til til = tilRepository.findByStepIdAndUserId(step.getId(), user.getId());
            maps.put(step, til);
        }

        // 해당 페이지로 들어온 사람의 role (master, manager, member, none)
        Optional<UserRoadmap> userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId());
        String myRole = userRoadmap.map(UserRoadmap::getRole).orElse("none");
        int progress = userRoadmap.map(UserRoadmap::getProgress).orElse(0);

        List<StepResponse.FindAllStepDTO.StepDTO> stepDTOs = steps.stream()
                .map(step -> new StepResponse.FindAllStepDTO.StepDTO(step, maps.get(step))).collect(Collectors.toList());

        return new StepResponse.FindAllStepDTO(stepDTOs, progress, myRole);
    }

    // step 삭제하기
    @Transactional
    public void deleteStep(Long stepId, User user){
        Step step = getStepById(stepId);

        checkMasterAndManagerPermission(step.getRoadmap().getId(), user); // 매니저급만 삭제 가능

        Roadmap roadmap = step.getRoadmap();
        roadmap.subStepNum();

        // 1. Til을 삭제한다.
        List<Long> tilIds = getTilsByStepId(stepId).stream().map(Til::getId).collect(Collectors.toList());
//        List<Long> tilIds = tils.stream().map(Til::getId).collect(Collectors.toList());

        tilRepository.softDeleteTilsByTilIds(tilIds);

        // 2. Til과 연관된 Comment들을 삭제한다.
        List<Comment> comments = getCommentsByTilIds(tilIds);
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        commentRepository.softDeleteCommentsByIds(commentIds);

        // 3. Comment와 관련된 알람을 삭제한다.
        alarmRepository.deleteByCommentIds(commentIds);

        // 4. Reference들을 삭제한다.
        referenceRepository.softDeleteReferenceByStepId(stepId);

        // 5. UserStep을 삭제한다
        userStepRepository.softDeleteUserStepByStepId(stepId);

        // 6. Step을 삭제한다
        stepRepository.softDeleteStepById(stepId);


    }

    // 로드맵의 관리자 권한 확인 (master, manager)
    private String checkMasterAndManagerPermission(Long roadmapId, User user) {
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, user.getId());

        if(!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()) && !userRoadmap.getRole().equals(GroupRole.ROLE_MANAGER.getValue())){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }

        return userRoadmap.getRole();
    }

    private Step getStepById(Long stepId) {
        return stepRepository.findById(stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }

    private List<Til> getTilsByStepId(Long stepId){
        return tilRepository.findByStepId(stepId);
    }

    private List<Comment> getCommentsByTilIds( List<Long> tilIds){
        return commentRepository.findByTilIds(tilIds);
    }

    // 해당 로드맵에 속한 user
    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }
}
