package com.example.tily.step;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StepService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;

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

    @Transactional
    public StepResponse.FindReferenceDTO findReference(Long stepId){
        Step step = stepRepository.findById(stepId).orElseThrow(
                () -> new Exception400("해당 스텝을 찾을 수 없습니다")
        );

        List<Reference> referenceList = referenceRepository.findByStep(step);

        List<StepResponse.FindReferenceDTO.YoutubeLink> youtubeLinks = new ArrayList<>();
        List<StepResponse.FindReferenceDTO.ReferenceLink> referenceLinks = new ArrayList<>();

        for(Reference reference : referenceList){
            String category = reference.getCategory();
            Long id = reference.getId();
            String link = reference.getLink();

            if(category.equals("youtube") ) {
                StepResponse.FindReferenceDTO.YoutubeLink youtubeLink = new StepResponse.FindReferenceDTO.YoutubeLink(id, link);
                youtubeLinks.add(youtubeLink);
            }
            else if(category.equals("reference") ) {
                StepResponse.FindReferenceDTO.ReferenceLink referenceLink = new StepResponse.FindReferenceDTO.ReferenceLink(id, link);
                referenceLinks.add(referenceLink);
            }
        }

        return new StepResponse.FindReferenceDTO(step, youtubeLinks, referenceLinks);
    }
}
