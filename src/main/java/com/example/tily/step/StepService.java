package com.example.tily.step;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;
import com.example.tily.comment.CommentRepository;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.reference.Reference;
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

    // 개인 로드맵(카테고리)의 step 생성하기
    @Transactional
    public StepResponse.CreateIndividualStepDTO createIndividualStep(Long id, StepRequest.CreateIndividualStepDTO requestDTO, User user){

        Roadmap roadmap = getRoadmapById(id);

        // 사용자가 해당 로드맵에 속했는지 확인
        userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(id, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_ROADMAP_FORBIDDEN));

        Step step = Step.builder().roadmap(roadmap).title(requestDTO.title()).build(); // 개인 로드맵이므로 description, dueDate 는 null
        stepRepository.save(step);

        UserStep userStep = UserStep.builder().roadmap(roadmap).step(step).user(user).isSubmit(true).build();
        userStepRepository.save(userStep);

        return new StepResponse.CreateIndividualStepDTO(step);
    }

    // step 생성하기
    @Transactional
    public StepResponse.CreateStepDTO createStep(StepRequest.CreateStepDTO requestDTO, Long roadmapId, User user) {
        checkMasterAndManagerPermission(roadmapId, user); // 관리자 권한 확인

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Step step = Step.builder()
                .roadmap(roadmap)
                .title(requestDTO.title())
                .description(requestDTO.description())
                .dueDate(requestDTO.dueDate())
                .build();
        stepRepository.save(step);

        // 해당 로드맵에 속한 학생들에 대해, UserStep에 넣기
        List<User> users = userRoadmapRepository.findByRoadmapIdAndIsAcceptTrue(roadmapId).stream().map(UserRoadmap::getUser).toList();
        for (User u : users) {
            UserStep userStep = UserStep.builder()
                    .roadmap(roadmap)
                    .step(step)
                    .user(u)
                    .isSubmit(false)
                    .build();
            userStepRepository.save(userStep);
        }

        return new StepResponse.CreateStepDTO(step);
    }

    // step 수정하기
    public void updateStep(StepRequest.UpdateStepDTO requestDTO, Long roadmapId, Long stepId, User user) {
        checkMasterAndManagerPermission(roadmapId, user); // 관리자 권한 확인

        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        step.update(requestDTO);
    }

    // 로드맵의 step 목록 전체 조회
    public StepResponse.FindAllStepDTO findAllStep (Long roadmapId, User user) {

        List<Step> steps = stepRepository.findByRoadmapId(roadmapId);

        Map<Step, Til> maps = new HashMap<>();
        for (Step step : steps) {
            Til til = tilRepository.findByStepIdAndUserId(step.getId(), user.getId());
            maps.put(step, til);
        }

        // 해당 페이지로 들어온 사람의 역할 (master, manager, member, none)
        Optional<UserRoadmap> userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, user.getId());
        String myRole = userRoadmap.isPresent() ? userRoadmap.get().getRole() : "none";
        int progress = userRoadmap.isPresent() ? userRoadmap.get().getProgress() : 0;

        List<StepResponse.FindAllStepDTO.StepDTO> stepDTOs = steps.stream()
                .map(step -> new StepResponse.FindAllStepDTO.StepDTO(step, maps.get(step))).collect(Collectors.toList());
        return new StepResponse.FindAllStepDTO(stepDTOs, progress, myRole);
    }

    // step 삭제
    @Transactional
    public void deleteStep(Long stepId, User user){
        Step step = getStepById(stepId);

        checkMasterAndManagerPermission(step.getRoadmap().getId(), user); // 매니저급만 삭제 가능

        List<Til> tils = tilRepository.findByStepId(stepId);
        List<Long> tilIds = tils.stream()
                .map(Til::getId)
                .collect(Collectors.toList());

        // 1. Til과 연관된 Comment들을 삭제한다.
        commentRepository.softDeleteCommentsByTilIds(tilIds);

        // 2. Til들을 삭제한다
        tilRepository.softDeleteTilsByTilIds(tilIds);

        // 3. Reference들을 삭제한다.
        referenceRepository.softDeleteReferenceByStepId(stepId);

        // 4. UserStep을 삭제한다
        userStepRepository.softDeleteUserStepByStepId(stepId);

        // 5. Step을 삭제한다
        stepRepository.delete(step);
    }

    private String checkMasterAndManagerPermission(Long roadmapId, User user) { // 매니저급만 접근
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, user.getId());

        if(!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()) && !userRoadmap.getRole().equals(GroupRole.ROLE_MANAGER.getValue())){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }
        return userRoadmap.getRole();
    }

    private Roadmap getRoadmapById(Long roadmapId) {
        return roadmapRepository.findById(roadmapId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
    }

    private Step getStepById(Long stepId) {
        return stepRepository.findById(stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));
    }

    // 해당 로드맵에 속한 user
    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }
}
