package com.example.tily.step;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StepService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;

    @Transactional
    public StepResponse.CreateIndividualStepDTO createIndividualStep(Long id, StepRequest.CreateIndividualStepDTO requestDTO){
        Roadmap roadmap = roadmapRepository.findById(id).orElseThrow(
                () -> new Exception400("해당 로드맵을 찾을 수 없습니다")
        );
        String title = requestDTO.getTitle();

        Step step = Step.builder().roadmap(roadmap).title(title).build();

        stepRepository.save(step);

        return new StepResponse.CreateIndividualStepDTO(step);
    }
}
