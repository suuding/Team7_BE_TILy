package com.example.tily.til;

import com.example.tily._core.errors.exception.Exception400;
import com.example.tily.step.Step;
import com.example.tily.step.StepRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
    public TilResponse.FindAllDTO findAllMyTil(Long roadmapId, String date, String title, int page, int size, User user) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        if (date!=null) {
            try {
                LocalDate now = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDateTime startDate = LocalDateTime.of(now, LocalTime.of( 0,0,0));
                LocalDateTime endDate = LocalDateTime.of(now, LocalTime.of( 23,59,59));

                Slice<Til> tils = tilRepository.findAllByDateByOrderByCreatedDateDesc(user.getId(), roadmapId, startDate, endDate, title, pageable);
                return new TilResponse.FindAllDTO(tils);
            } catch (Exception e) {
                throw new Exception400("입력한 날짜를 찾을 수 없습니다.");
            }
        } else {
            Slice<Til> tils = tilRepository.findAllByOrderByCreatedDateDesc(user.getId(), roadmapId, title, pageable);
            return new TilResponse.FindAllDTO(tils);
        }
    }
}
