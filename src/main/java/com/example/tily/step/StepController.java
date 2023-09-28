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
    public ResponseEntity<?> createStep(@PathVariable int id, @RequestBody @Valid StepRequest.CreateStepDTO requestDTO) {
        StepResponse.CreateStepDTO responseDTO = stepService.createIndividual(id, requestDTO);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(responseDTO);

        return ResponseEntity.ok(apiResult);
    }
}