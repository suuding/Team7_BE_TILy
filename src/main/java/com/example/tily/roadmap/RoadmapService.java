package com.example.tily.roadmap;

import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoadmapService {
    private final RoadmapRepository roadmapRepository;

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
}
