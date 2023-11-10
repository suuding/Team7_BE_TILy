package com.example.tily.step;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // step 생성하기
    @PostMapping("/steps")
    public ResponseEntity<?> createStep(@RequestBody @Valid StepRequest.CreateStepDTO requestDTO, Errors errors,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        StepResponse.CreateStepDTO responseDTO = stepService.createStep(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.CREATED, responseDTO));
    }

    // step 수정하기
    @PatchMapping("/steps/{stepId}")
    public ResponseEntity<?> updateStep(@PathVariable Long stepId,
                                        @RequestBody @Valid StepRequest.UpdateStepDTO requestDTO, Errors errors,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        stepService.updateStep(stepId, requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }

    // 특정 로드맵의 step 목록 전체 조회
    @GetMapping("/roadmaps/{roadmapId}/steps")
    public ResponseEntity<?> findAllStep(@PathVariable("roadmapId") Long roadmapId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        StepResponse.FindAllStepDTO responseDTO = stepService.findAllStep(roadmapId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, responseDTO));
    }

    // step 삭제
    @DeleteMapping("/steps/{stepId}")
    public ResponseEntity<?> deleteStep(@PathVariable Long stepId, @AuthenticationPrincipal CustomUserDetails userDetails){
        stepService.deleteStep(stepId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(HttpStatus.OK, null));
    }
}