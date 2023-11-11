package com.example.tily.step.reference;

import com.example.tily._core.errors.CustomException;
import com.example.tily._core.errors.ExceptionCode;
import com.example.tily.roadmap.relation.GroupRole;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.StepResponse;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final StepRepository stepRepository;
    private final UserRoadmapRepository userRoadmapRepository;

    // step의 참고자료 생성하기
    @Transactional
    public void createReference(ReferenceRequest.CreateReferenceDTO requestDTO, User user) {
        Long stepId = requestDTO.stepId();
        Step step = getStepById(stepId);

        checkMasterAndManagerPermission(step.getRoadmap().getId(), user); // 생성자 혹 매니저만 생성 가능

        Reference reference = Reference.builder()
                .step(step)
                .category(requestDTO.category())
                .link(requestDTO.link())
                .build();

        referenceRepository.save(reference);
    }

    // step의 참고자료 목록 조회하기
    public StepResponse.FindReferenceDTO findReference(Long stepId, User user){
        Step step = getStepById(stepId);

        List<Reference> references = referenceRepository.findByStepId(stepId);

        List<StepResponse.FindReferenceDTO.YoutubeDTO> youtubeDTOs = new ArrayList<>();
        List<StepResponse.FindReferenceDTO.WebDTO> webDTOs = new ArrayList<>();

        for(Reference reference : references){
            String category = reference.getCategory();
            Long referenceId = reference.getId();
            String link = reference.getLink();

            if(category.equals("youtube"))
                youtubeDTOs.add(new StepResponse.FindReferenceDTO.YoutubeDTO(referenceId, link));
            else if(category.equals("web"))
                webDTOs.add(new StepResponse.FindReferenceDTO.WebDTO(referenceId, link));
        }

        return new StepResponse.FindReferenceDTO(step, youtubeDTOs, webDTOs);
    }

    // 참고자료 삭제
    public void deleteReference(Long referenceId, User user){
        Reference reference = getReferenceById(referenceId);

        checkMasterAndManagerPermission(reference.getStep().getRoadmap().getId(), user); // 생성자 혹 매니저만 삭제 가능

        referenceRepository.softDeleteReferenceById(referenceId);
    }

    private String checkMasterAndManagerPermission(Long roadmapId, User user) { // 매니저급만 접근
        UserRoadmap userRoadmap = getUserBelongRoadmap(roadmapId, user.getId());

        if(!userRoadmap.getRole().equals(GroupRole.ROLE_MASTER.getValue()) && !userRoadmap.getRole().equals(GroupRole.ROLE_MANAGER.getValue())){
            throw new CustomException(ExceptionCode.ROADMAP_FORBIDDEN);
        }

        return userRoadmap.getRole();
    }

    private Step getStepById(Long stepId) {
        return stepRepository.findById(stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));
    }

    private Reference getReferenceById(Long referenceId){
        return referenceRepository.findById(referenceId).orElseThrow(() -> new CustomException(ExceptionCode.REFERENCE_NOT_FOUND));
    }

    private UserRoadmap getUserBelongRoadmap(Long roadmapId, Long userId) {
        return userRoadmapRepository.findByRoadmapIdAndUserIdAndIsAcceptTrue(roadmapId, userId).orElseThrow(
                () -> new CustomException(ExceptionCode.ROADMAP_NOT_BELONG));
    }
}
