package com.example.tily.step;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/roadmaps/{roadmapsId}/steps/{stepsId}/references")
    public ResponseEntity<?> findReference(@PathVariable Long stepsId){
        StepResponse.FindReferenceDTO responseDTO = stepService.findReference(stepsId);

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 특정 로드맵의 step 목록 전체 조회
    @GetMapping("/roadmaps/{roadmapId}/steps")
    public ResponseEntity<?> findAllStep(@PathVariable("roadmapId") Long roadmapId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        StepResponse.FindAllStepDTO responseDTO = stepService.findAllStep(roadmapId, customUserDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}