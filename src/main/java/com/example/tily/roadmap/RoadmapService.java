package com.example.tily.roadmap;

import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;

    @Transactional
    public RoadmapResponse.CreateIndividualDTO createIndividual(RoadmapRequest.CreateIndividualDTO requestDTO, User user){
        String creator = user.getName();
        String category = "individual";
        String name = requestDTO.getName();
        Long stepNum = 0L;

        Roadmap roadmap = Roadmap.builder().creator(creator).category(category).name(name).stepNum(stepNum).build();

        roadmapRepository.save(roadmap);

        return new RoadmapResponse.CreateIndividualDTO(roadmap);
    }

    @Transactional
    public RoadmapResponse.createGroupRoadmapDTO createGroupRoadmap(RoadmapRequest.CreateGroupRoadmapDTO requestDTO, User user){
        // repository 저장
        String creator = user.getName();
        String category = "group";
        String name = requestDTO.getRoadmap().getName();
        String roadmapDescription = requestDTO.getRoadmap().getDescription();
        Boolean isPublic = requestDTO.getRoadmap().getIsPublic();
        Long currentNum = 1L; // 현재 인원수는 creator 한 명
        String code = generateRandomCode();
        Boolean isRecruit = true; // 그룹 로드맵이기 때문
        Long stepNum = (long) requestDTO.getSteps().size();

        Roadmap roadmap = Roadmap.builder().creator(creator).category(category).name(name).description(roadmapDescription).isPublic(isPublic).currentNum(currentNum).code(code).isRecruit(isRecruit).stepNum(stepNum).build();
        roadmapRepository.save(roadmap);

        List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs> stepDTOs = requestDTO.getSteps();

        for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs stepDTO : stepDTOs){
            // step 저장
            String title = stepDTO.getTitle() ;
            String stepDescription = stepDTO.getDescription();

            Step step = Step.builder().roadmap(roadmap).title(title).description(stepDescription).build();
            stepRepository.save(step);

            // reference 저장
            RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs.ReferenceDTO referenceDTOs = stepDTO.getReferences();

            // (1) youtube
            List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs.ReferenceDTO.Link> youtubeList = referenceDTOs.getYoutube();
            for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs.ReferenceDTO.Link youtubeDTO : youtubeList){
                String link = youtubeDTO.getLink();

                Reference reference = Reference.builder().step(step).category("youtube").link(link).build();
                referenceRepository.save(reference);
            }

            // (2) reference
            List<RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs.ReferenceDTO.Link> referenceList = referenceDTOs.getReference();
            for(RoadmapRequest.CreateGroupRoadmapDTO.StepDTOs.ReferenceDTO.Link referenceDTO : referenceList){
                String link = referenceDTO.getLink();

                Reference reference = Reference.builder().step(step).category("reference").link(link).build();
                referenceRepository.save(reference);
            }
        }

        return new RoadmapResponse.createGroupRoadmapDTO(roadmap);
    }


    private static String generateRandomCode() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String combinedChars = upperAlphabet + lowerAlphabet + numbers;

        Random random = new Random();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(combinedChars.length());
            char randomChar = combinedChars.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
