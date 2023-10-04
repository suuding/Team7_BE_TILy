package com.example.tily.til;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TilService {
    private final TilRepository tilRepository;
    private final StepRepository stepRepository;

    @Transactional
    public TilResponse.CreateTilDTO createTil(TilRequest.CreateTilDTO requestDTO){

        String title = requestDTO.getTitle();
        Til til = Til.builder().title(title).build();
        tilRepository.save(til);

        return new TilResponse.CreateTilDTO(til);
    }

    @Transactional
    public void updateTil(TilRequest.UpdateTilDTO requestDTO, Long id) {

        Til til = tilRepository.findById(id).orElseThrow(
                () -> new Exception400("해당 til을 찾을 수 없습니다.")
        );

        String content = requestDTO.getContent();
        if(content == null){
            throw new Exception400("TIL 내용을 입력해주세요.");
        }
        til.updateContent(content);
    }

    public TilResponse.ViewDTO viewTil(Long tilId, Long stepId) {
        Til til = tilRepository.findTilById(tilId);
        Step step = stepRepository.findById(stepId).orElseThrow(
                () -> new Exception400("해당 스텝을 찾을 수 없습니다. ")
        );

        return new TilResponse.ViewDTO(step, til);
    }

    @Transactional
    public void submitTil(TilRequest.SubmitTilDTO requestDTO, Long id) {

        Til til = tilRepository.findById(id).orElseThrow(
                () -> new Exception400("해당 til을 찾을 수 없습니다.")
        );

        String submitContent = requestDTO.getSubmitContent();
        if(submitContent == null){
            throw new Exception400("TIL 내용을 입력해주세요.");
        }

    }

    @Transactional
    public void deleteTil(Long id) {
        Optional<Til> til = tilRepository.findById(id);

        if(til.isPresent()) {
            tilRepository.deleteById(id);
        }
    }
}
