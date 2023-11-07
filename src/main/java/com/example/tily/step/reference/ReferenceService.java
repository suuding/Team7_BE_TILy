package com.example.tily.step.reference;

import com.example.tily._core.errors.exception.CustomException;
import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
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
    private final RoadmapRepository roadmapRepository;

    // step의 참고자료 생성하기
    @Transactional
    public void createReference(ReferenceRequest.CreateReferenceDTO requestDTO, Long roadmapId, Long stepId, User user) {
        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));

        Step step = getStepById(stepId);

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
            Long id = reference.getId();
            String link = reference.getLink();

            if(category.equals("youtube"))
                youtubeDTOs.add(new StepResponse.FindReferenceDTO.YoutubeDTO(id, link));
            else if(category.equals("web"))
                webDTOs.add(new StepResponse.FindReferenceDTO.WebDTO(id, link));
        }

        return new StepResponse.FindReferenceDTO(step, youtubeDTOs, webDTOs);
    }

    private Step getStepById(Long stepId) {
        return stepRepository.findById(stepId).orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));
    }
}
