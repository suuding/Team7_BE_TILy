package com.example.tily.til;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.roadmap.RoadmapRequest;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.step.StepResponse;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TilService {
    private final TilRepository tilRepository;
    private final StepRepository stepRepository;

    @Transactional
    public TilResponse.CreateTilDTO createTil(){

        Til til = Til.builder().build();
        tilRepository.save(til);

        return new TilResponse.CreateTilDTO(til);
    }
}
