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
    private final ReferenceRepository referenceRepository;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;
    private final UserStepRepository userStepRepository;
    private final CommentRepository commentRepository;

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

    // step의 참고자료 목록 조회
    public StepResponse.FindReferenceDTO findReference(Long stepId){
        Step step = getStepById(stepId);

        List<Reference> references = referenceRepository.findByStepId(stepId);

        List<StepResponse.FindReferenceDTO.YoutubeDTO> youtubeDTOs = new ArrayList<>();
        List<StepResponse.FindReferenceDTO.WebDTO> webDTOs = new ArrayList<>();

        for(Reference reference : references){
            String category = reference.getCategory();
            Long id = reference.getId();
            String link = reference.getLink();

            if(category.equals("youtube"))
                youtubeDTOs.add(new StepResponse.FindReferenceDTO.YoutubeDTO(id, link));
            else if(category.equals("web"))
                webDTOs.add(new StepResponse.FindReferenceDTO.WebDTO(id, link));
        }

        return new StepResponse.FindReferenceDTO(step, youtubeDTOs, webDTOs);
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

        List<Reference> references = referenceRepository.findByStepId(stepId);
        List<Long> referenceIds = references.stream()
                .map(Reference::getId)
                .collect(Collectors.toList());

        // 1. Til과 연관된 Comment들을 삭제한다.
        commentRepository.softDeleteAllCommentsByTilIds(tilIds);

        // 2. Til들을 삭제한다
        tilRepository.softDeleteAllTils(tilIds);

        // 3. Reference들을 삭제한다.
        referenceRepository.softDeleteAllReferences(referenceIds);

        // 4. UserStep을 삭제한다
        UserStep userStep = getUserStepByUserIdAndStepId(user.getId(), stepId);
        userStepRepository.delete(userStep);

        // 5. Step을 삭제한다
        stepRepository.delete(step);
    }

    // 참고자료 삭제
    public void deleteReference(Long referenceId, User user){
        Reference reference = getReferenceById(referenceId);

        checkMasterAndManagerPermission(reference.getStep().getRoadmap().getId(), user); // 매니저급만 삭제 가능

        referenceRepository.delete(reference);
    }

    private String checkMasterAndManagerPermission(Long roadmapId, User user) {
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

    private Reference getReferenceById(Long referenceId){
        return referenceRepository.findById(referenceId).orElseThrow(() -> new CustomException(ExceptionCode.REFERENCE_NOT_FOUND));
    }

    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId).orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }

    private UserStep getUserStepByUserIdAndStepId(Long userId, Long stepId){
        return userStepRepository.findByUserIdAndStepId(userId, stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_INCLUDE));
    }
}
