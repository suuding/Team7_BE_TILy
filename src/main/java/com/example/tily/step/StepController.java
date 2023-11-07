package com.example.tily.step;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StepController {
    private final StepService stepService;

    // 개인 로드맵(카테고리)의 step 생성하기
    @PostMapping("/roadmaps/individual/{id}/steps")
    public ResponseEntity<?> createIndividualStep(@RequestBody @Valid StepRequest.CreateIndividualStepDTO requestDTO, Errors errors,
                                                  @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        StepResponse.CreateIndividualStepDTO responseDTO = stepService.createIndividualStep(id, requestDTO, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 특정 로드맵의 step 생성
    @PostMapping("/roadmaps/{roadmapId}/steps")
    public ResponseEntity<?> createStep(@RequestBody @Valid StepRequest.CreateStepDTO requestDTO, Errors errors,
                                        @PathVariable("roadmapId") Long roadmapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        stepService.createStep(requestDTO, roadmapId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 특정 로드맵의 step 목록 전체 조회
    @GetMapping("/roadmaps/{roadmapId}/steps")
    public ResponseEntity<?> findAllStep(@PathVariable("roadmapId") Long roadmapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        StepResponse.FindAllStepDTO responseDTO = stepService.findAllStep(roadmapId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 특정 로드맵의 step 수정
    @PostMapping("/roadmaps/{roadmapId}/steps/{stepId}")
    public ResponseEntity<?> createStep(@RequestBody @Valid StepRequest.UpdateStepDTO requestDTO, Errors errors,
                                        @PathVariable("roadmapId") Long roadmapId, @PathVariable("stepId") Long stepId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        stepService.updateStep(requestDTO, roadmapId, stepId, userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(null));
    }
}