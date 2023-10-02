package com.example.tily.step;

import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class StepController {
    private final StepService stepService;

    // 개인 로드맵(카테고리)의 step 생성하기
    @PostMapping("/roadmaps/individual/{id}/steps")
    public ResponseEntity<?> createIndividualStep(@PathVariable Long id, @RequestBody @Valid StepRequest.CreateIndividualStepDTO requestDTO) {
        StepResponse.CreateIndividualStepDTO responseDTO = stepService.createIndividualStep(id, requestDTO);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 특정 step의 참고자료 목록 조회
    @PostMapping("/roadmaps/{id1}/steps/{id2}/references")
    public ResponseEntity<?> findReference(@PathVariable(value = "id2") Long stepId){
        StepResponse.FindReferenceDTO responseDTO = stepService.findReference(stepId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}