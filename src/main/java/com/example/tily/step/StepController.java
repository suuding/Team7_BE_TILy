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
}